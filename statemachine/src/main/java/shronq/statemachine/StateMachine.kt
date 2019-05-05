package shronq.statemachine

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.awaitFirst
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
 *
 * TODO: Nested engines?
 */

@FlowPreview
@ObsoleteCoroutinesApi
class StateMachine<StateT : Any, EventT : Any>(
    private val coroutineScope: CoroutineScope,
    initialState: StateT,
    private val events: Observable<out EventT>,
    private val initializer: EngineContext<StateT, EventT>.() -> Unit
) {
  private val transitions = PublishRelay.create<StateTransition<StateT>>().toSerialized()
  private val eventContext = object : EventContext<StateT> {
    override fun enterState(block: StateTransition<StateT>) {
      transitions.accept(block)
    }

    override suspend fun getLatestState(): StateT {
      return states.awaitFirst()
    }
  }
  val states: Observable<StateT>

  init {
    val connectableStates = transitions.scan(initialState) { state, transition ->
      val ctx = object : StateEditorContext<StateT> {
        override val state = state
      }

      ctx.transition()
    }.replay(1)

    // Immediately subscribe to catch all states in the replay. There's no chance
    // of upstream resources to leak or errors to be emitted, so that's okay.
    // TODO: There's actually a chance of throwing an error from the transition lambda D:
    connectableStates.autoConnect(0)

    states = connectableStates
  }

  fun start() {
    val ctx = EngineContext<StateT, EventT>()
    ctx.initializer()

    for (binding in ctx.eventBindings) {
      @Suppress("UNCHECKED_CAST")
      val casted = binding as ListenerBinding<StateT, EventT>
      coroutineScope.launch {
        var filtered = events.filter { it.javaClass == casted.eventClass }
        if (casted.distinct) {
          filtered = filtered.distinctUntilChanged()
        }
        filtered.openSubscription().consumeEach { event ->
          casted.listener(eventContext, event)
        }
      }
    }

    for (binding in ctx.firstEventBindings) {
      coroutineScope.launch {
        events
            .filter { it.javaClass == binding.eventClass }
            .firstElement()
            .openSubscription()
            .consumeEach { event ->
              @Suppress("UNCHECKED_CAST")
              val casted = binding as ListenerBinding<StateT, EventT>
              casted.listener(eventContext, event)
            }
      }
    }

    val streamContext = object : StreamContext<StateT> {
      override fun <T> Observable<T>.hookUp(): HookedUpSubscription {
        return hookUpInternal(null)
      }

      override fun <T> Observable<T>.hookUp(block: EventContext<StateT>.(T) -> Unit): HookedUpSubscription {
        return hookUpInternal(block)
      }

      private fun <T> Observable<T>.hookUpInternal(block: (EventContext<StateT>.(T) -> Unit)?): HookedUpSubscription {
        coroutineScope.launch {
          this@hookUpInternal
              .openSubscription()
              .consumeEach { block?.invoke(eventContext, it) }
        }
        return HookedUpSubscription()
      }
    }

    for (binding in ctx.streamBindings) {
      val filteredEvents = events.filter { it.javaClass == binding.eventClass }
      @Suppress("UNCHECKED_CAST")
      val casted = binding as StreamBinding<StateT, EventT>
      casted.block(streamContext, filteredEvents)
    }

    for (binding in ctx.externalStreamBindings) {
      binding.block(streamContext)
    }

    val flowContext = object : FlowContext<StateT> {
      override fun <T> Flow<T>.hookUp(): HookedUpSubscription {
        coroutineScope.launch {
          collect { }
        }
        return HookedUpSubscription()
      }

      override fun <T> Flow<T>.hookUp(block: EventContext<StateT>.(T) -> Unit): HookedUpSubscription {
        coroutineScope.launch {
          collect { block(eventContext, it) }
        }
        return HookedUpSubscription()
      }
    }

    for (binding in ctx.externalFlowBindings) {
      binding.block(flowContext)
    }

    coroutineScope.launch {
      ctx.onInitCallback?.invoke(eventContext)
    }
  }
}

@FlowPreview
class EngineContext<StateT, EventT> internal constructor() {
  @PublishedApi internal val eventBindings = mutableListOf<ListenerBinding<StateT, out EventT>>()
  @PublishedApi internal val firstEventBindings = mutableListOf<ListenerBinding<StateT, out EventT>>()
  @PublishedApi internal val streamBindings = mutableListOf<StreamBinding<StateT, out EventT>>()
  @PublishedApi internal val externalStreamBindings = mutableListOf<ExternalBinding<StateT>>()
  @PublishedApi internal val externalFlowBindings = mutableListOf<FlowBinding<StateT>>()

  internal var onInitCallback: (suspend EventContext<StateT>.() -> Unit)? = null

  fun onInit(block: suspend EventContext<StateT>.() -> Unit) {
    onInitCallback = block
  }

  inline fun <reified T : EventT> on(noinline block: suspend EventContext<StateT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block)
    eventBindings.add(binding)
  }

  inline fun <reified T : EventT> onDistinct(noinline block: suspend EventContext<StateT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block, distinct = true)
    eventBindings.add(binding)
  }

  inline fun <reified T : EventT> onFirst(noinline block: suspend EventContext<StateT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block)
    firstEventBindings.add(binding)
  }

  inline fun <reified T : EventT> streamOf(noinline block: StreamContext<StateT>.(Observable<out T>) -> HookedUpSubscription) {
    val binding = StreamBinding(T::class.java, block)
    streamBindings.add(binding)
  }

  fun externalStream(block: StreamContext<StateT>.() -> HookedUpSubscription) {
    val binding = ExternalBinding(block)
    externalStreamBindings.add(binding)
  }

  fun externalFlow(block: FlowContext<StateT>.() -> HookedUpSubscription) {
    val binding = FlowBinding(block)
    externalFlowBindings.add(binding)
  }
}

typealias StateTransition<StateT> = StateEditorContext<StateT>.() -> StateT

class ListenerBinding<StateT, SpecificIntentT>(
    val eventClass: Class<out SpecificIntentT>,
    val listener: suspend EventContext<StateT>.(SpecificIntentT) -> Unit,
    val distinct: Boolean = false
)

class StreamBinding<StateT, SpecificIntentT>(
    val eventClass: Class<out SpecificIntentT>,
    val block: StreamContext<StateT>.(Observable<out SpecificIntentT>) -> HookedUpSubscription
)

interface EventContext<StateT> {
  fun enterState(block: StateTransition<StateT>)
  suspend fun getLatestState(): StateT
}

interface StateEditorContext<StateT> {
  val state: StateT
}

interface StreamContext<StateT> {
  fun <T> Observable<T>.hookUp(): HookedUpSubscription
  fun <T> Observable<T>.hookUp(block: EventContext<StateT>.(T) -> Unit): HookedUpSubscription
}

@FlowPreview
interface FlowContext<StateT> {
  fun <T> Flow<T>.hookUp(): HookedUpSubscription
  fun <T> Flow<T>.hookUp(block: EventContext<StateT>.(T) -> Unit): HookedUpSubscription
}

/** Enforces usage of `StreamContext.hookUp`. */
class HookedUpSubscription internal constructor()

class ExternalBinding<StateT>(
    val block: StreamContext<StateT>.() -> HookedUpSubscription
)

@FlowPreview
class FlowBinding<StateT>(
    val block: FlowContext<StateT>.() -> HookedUpSubscription
)
