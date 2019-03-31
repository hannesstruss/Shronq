package shronq.mvi

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope

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

class MviEngine<StateT : Any, IntentT : Any> private constructor(
    initialState: StateT,
    intents: Observable<out IntentT>,
    initializer: EngineContext<StateT, IntentT>.() -> Unit
) {
  companion object {
    fun <StateT : Any, IntentT : Any> create(
        initialState: StateT,
        intents : Observable<out IntentT>,
        initializer: EngineContext<StateT, IntentT>.() -> Unit
    ): MviEngine<StateT, IntentT> {
      return MviEngine(initialState, intents, initializer)
    }
  }

  val states: Observable<StateT> = Observable.never()
}

class EngineContext<StateT, IntentT> internal constructor(
    private val coroutineScope: CoroutineScope
) {
  private val disposable = CompositeDisposable()
  val intentBindings = mutableListOf<ListenerBinding<StateT, out IntentT>>()
  val firstIntentBindings = mutableListOf<ListenerBinding<StateT, out IntentT>>()
  val streamListeners = mutableMapOf<Class<out IntentT>, StreamContext<StateT>.(Observable<out IntentT>) -> StreamContextToken>()

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

  inline fun <reified T : IntentT> streamOf(noinline block: StreamContext<StateT>.(Observable<T>) -> StreamContextToken) {
    TODO("not implemented")
  }

  fun externals(block: ExternalsContext<StateT>.() -> Unit) {
    TODO("not implemented")
  }
}

class ListenerBinding<StateT, SpecificIntentT>(
    val intentClass: Class<out SpecificIntentT>,
    val listener: suspend IntentContext<StateT>.(SpecificIntentT) -> Unit
)

interface IntentContext<StateT> {
  fun enterState(block: StateEditorContext<StateT>.() -> StateT)
}

interface StateEditorContext<StateT> {
  val state: StateT
}

interface StreamContext<StateT> {
  fun <T> Observable<T>.hookUp(): StreamContextToken
  fun <T> Observable<T>.hookUp(block: IntentContext<StateT>.(T) -> Unit): StreamContextToken
}

// Force usage of `hookUp` in `streamOf` TODO: make more forceful and less abusable
interface StreamContextToken

interface ExternalsContext<StateT> {
  fun <T> Observable<T>.hookUp(block: suspend ExternalsItemContext<StateT>.(T) -> Unit)
}

interface ExternalsItemContext<StateT> {
  fun enterState(block: StateEditorContext<StateT>.() -> StateT)
}

