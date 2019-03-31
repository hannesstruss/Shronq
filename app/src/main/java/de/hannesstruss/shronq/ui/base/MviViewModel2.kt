package de.hannesstruss.shronq.ui.base

import de.hannesstruss.shronq.ui.base.MyIntent.CountDown
import de.hannesstruss.shronq.ui.base.MyIntent.CountUp
import io.reactivex.Observable
import kotlinx.coroutines.delay
import timber.log.Timber
import java.time.LocalTime
import java.util.concurrent.TimeUnit

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

abstract class MviViewModel2<StateT : Any, IntentT : Any> {
  val states: Observable<out StateT> = TODO()
  val intents: Observable<out IntentT> = TODO()

  abstract val engine: MviEngine

  open fun onEnterState(previous: StateT, next: StateT) {}

  protected fun engineContext(block: EngineContext<StateT, IntentT>.() -> Unit): MviEngine = TODO()
}

interface MviEngine

interface EngineContext<StateT, IntentT> {
  fun onInit(block: suspend () -> Unit)
  fun <T : IntentT> on(block: suspend IntentContext<StateT>.(T) -> Unit)
  fun <T : IntentT> onFirst(block: suspend IntentContext<StateT>.(T) -> Unit)
  fun <T : IntentT> streamOf(block: StreamContext<StateT>.(Observable<T>) -> StreamContextToken)
  fun externals(block: ExternalsContext<StateT>.() -> Unit)
}

interface IntentContext<StateT> {
  fun enterState(block: StateEditorContext<StateT>.() -> StateT)
}

interface StateEditorContext<StateT> {
  val state: StateT
}

typealias StateEditor<StateT> = (StateT) -> StateT

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


data class MyState(val counter: Int = 0, val secondsSum: Int = 0) {
  fun incrementCounter() = copy(counter = counter + 1)
  fun decrementCounter() = copy(counter = counter - 1)
  fun addSeconds(seconds: Int) = copy(secondsSum = secondsSum + seconds)
}

sealed class MyIntent {
  object CountUp : MyIntent()
  object CountDown : MyIntent()
}

object MyTimeService {
  val times: Observable<LocalTime> = TODO()
}

class MyViewModel : MviViewModel2<MyState, MyIntent>() {
  override val engine = engineContext {
    onInit {
      Timber.d("Hello!")
      delay(200)
      Timber.d("Hello again!")
    }

    // TODO: nested view model engines
    // nest(myNestedEngine) { nestedState -> ... }

    on<CountUp> {
      delay(1000)
      enterState { state.incrementCounter() }
    }

    streamOf<CountDown> { intents ->
      intents
          .sample(1, TimeUnit.SECONDS)
          .hookUp {
            enterState { state.decrementCounter() }
          }
    }

    streamOf<CountUp> { intents ->
      intents
          .sample(1, TimeUnit.SECONDS)
          .doOnNext { Timber.d("Here we're just doing side effects, for whatever reason") }
          .hookUp()
    }

    externals {
      MyTimeService.times
          .map { it.second }
          .hookUp {
            enterState { state.addSeconds(it) }
          }
    }
  }
}
