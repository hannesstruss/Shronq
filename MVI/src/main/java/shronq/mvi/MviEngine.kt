package shronq.mvi

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.openSubscription

/*
 * TODO
 *
 * Questions:
 *
 * # Is `enterState` cool, or do we need atomic state transitions ("Actions")?
 *
 * Often, actions merely describe setting a field on a state (`ShowLoading(true)`).
 * This in itself is already not ideal, rather an action should describe an event,
 * e.g. `StartedRefreshing` which the state/reducer can interpret as `loading = true`.
 *
 * Pro of having actions is that the set of state transitions is limited and
 * visible at one glance when looking at the action `sealed class`.
 *
 * Pro of having `enterState` is increased flexibility and less ceremony to get a screen
 * up and running.
 *
 * A middle ground might be `State` having "action methods" like `fun incrementCounter(): State`.
 *
 * Another middle ground might be allowing a special `EnterState(val state: State)` action
 * for certain engines. This encodes the state at the time of action creation though, so
 * instead we should use `typealias MyAction = (State) -> State`
 */

class MviEngine<StateT : Any, IntentT : Any>(
    private val coroutineScope: CoroutineScope,
    initialState: StateT,
    private val intents: Observable<out IntentT>,
    private val initializer: EngineContext<StateT, IntentT>.() -> Unit
) {
  private val transitions = PublishRelay.create<StateTransition<StateT>>().toSerialized()
  private val intentContext = object : IntentContext<StateT> {
    override fun enterState(block: StateTransition<StateT>) {
      transitions.accept(block)
    }
  }
  val states: Observable<StateT> = transitions.scan(initialState) { state, transition ->
    val ctx = object : StateEditorContext<StateT> {
      override val state = state
    }

    ctx.transition()
  }

  fun start() {
    val ctx = EngineContext<StateT, IntentT>()
    ctx.initializer()

    coroutineScope.launch {
      intents.openSubscription().consumeEach { intent ->
        for (binding in ctx.intentBindings) {
          if (binding.intentClass == intent.javaClass) {
            @Suppress("UNCHECKED_CAST")
            val casted = binding as ListenerBinding<StateT, IntentT>
            casted.listener(intentContext, intent)
          }
        }
      }
    }

    for (binding in ctx.firstIntentBindings) {
      coroutineScope.launch {
        intents
            .filter { it.javaClass == binding.intentClass }
            .firstElement()
            .openSubscription()
            .consumeEach { intent ->
              @Suppress("UNCHECKED_CAST")
              val casted = binding as ListenerBinding<StateT, IntentT>
              casted.listener(intentContext, intent)
            }
      }
    }

    val streamContext = object : StreamContext<StateT> {
      override fun <T> Observable<T>.hookUp(): HookedUpSubscription {
        return hookUpInternal(null)
      }

      override fun <T> Observable<T>.hookUp(block: IntentContext<StateT>.(T) -> Unit): HookedUpSubscription {
        return hookUpInternal(block)
      }

      private fun <T> Observable<T>.hookUpInternal(block: (IntentContext<StateT>.(T) -> Unit)?): HookedUpSubscription {
        coroutineScope.launch {
          this@hookUpInternal
              .openSubscription()
              .consumeEach { block?.invoke(intentContext, it) }
        }
        return HookedUpSubscription()
      }
    }

    for (binding in ctx.streamBindings) {
      val filteredIntentions = intents.filter { it.javaClass == binding.intentClass }
      @Suppress("UNCHECKED_CAST")
      val casted = binding as StreamBinding<StateT, IntentT>
      casted.block(streamContext, filteredIntentions)
    }

    for (binding in ctx.externalStreamBindings) {
      binding.block(streamContext)
    }

    coroutineScope.launch {
      ctx.onInitCallback?.invoke()
    }
  }
}

class EngineContext<StateT, IntentT> internal constructor() {
  val intentBindings = mutableListOf<ListenerBinding<StateT, out IntentT>>()
  val firstIntentBindings = mutableListOf<ListenerBinding<StateT, out IntentT>>()
  val streamBindings = mutableListOf<StreamBinding<StateT, out IntentT>>()
  val externalStreamBindings = mutableListOf<ExternalBinding<StateT>>()

  internal var onInitCallback: (suspend () -> Unit)? = null

  fun onInit(block: suspend () -> Unit) {
    onInitCallback = block
  }

  inline fun <reified T : IntentT> on(noinline block: suspend IntentContext<StateT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block)
    intentBindings.add(binding)
  }

  inline fun <reified T : IntentT> onFirst(noinline block: suspend IntentContext<StateT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block)
    firstIntentBindings.add(binding)
  }

  inline fun <reified T : IntentT> streamOf(noinline block: StreamContext<StateT>.(Observable<out T>) -> HookedUpSubscription) {
    val binding = StreamBinding(T::class.java, block)
    streamBindings.add(binding)
  }

  fun externalStream(block: StreamContext<StateT>.() -> HookedUpSubscription) {
    val binding = ExternalBinding(block)
    externalStreamBindings.add(binding)
  }
}

typealias StateTransition<StateT> = StateEditorContext<StateT>.() -> StateT

class ListenerBinding<StateT, SpecificIntentT>(
    val intentClass: Class<out SpecificIntentT>,
    val listener: suspend IntentContext<StateT>.(SpecificIntentT) -> Unit
)

class StreamBinding<StateT, SpecificIntentT>(
    val intentClass: Class<out SpecificIntentT>,
    val block: StreamContext<StateT>.(Observable<out SpecificIntentT>) -> HookedUpSubscription
)

interface IntentContext<StateT> {
  fun enterState(block: StateTransition<StateT>)
}

interface StateEditorContext<StateT> {
  val state: StateT
}

interface StreamContext<StateT> {
  fun <T> Observable<T>.hookUp(): HookedUpSubscription
  fun <T> Observable<T>.hookUp(block: IntentContext<StateT>.(T) -> Unit): HookedUpSubscription
}

/** Enforces usage of `StreamContext.hookUp`. */
class HookedUpSubscription internal constructor()

class ExternalBinding<StateT>(
    val block: StreamContext<StateT>.() -> HookedUpSubscription
)
