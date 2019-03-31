package de.hannesstruss.shronq.ui.base

import de.hannesstruss.shronq.ui.base.MyIntent.CountDown
import de.hannesstruss.shronq.ui.base.MyIntent.CountUp
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.coroutines.delay
import timber.log.Timber

abstract class MviViewModel2<StateT : Any, IntentT : Any> {
  val states: Observable<StateT> = TODO()

  abstract val engine: MviEngine

  open fun onEnterState(previous: StateT, next: StateT) {}

  protected fun engineContext(block: EngineContext<StateT, IntentT>.() -> Unit): MviEngine = TODO()
}

interface MviEngine

interface EngineContext<StateT, IntentT> {
  fun onInit(block: suspend () -> Unit)
  fun <T : IntentT> on(block: suspend IntentContext<StateT, T>.(T) -> Unit)
  fun <T : IntentT> onFirst(block: suspend IntentContext<StateT, T>.(T) -> Unit)
  fun <T : IntentT> streamOf(block: (Observable<T>) -> Observable<out StateT>)

  fun <T : IntentT, R> Observable<T>.withStates(block: (T, StateT) -> R)
}

interface IntentContext<StateT, IntentT> {
  val state: StateT
  fun enterState(state: StateT)
}

data class MyState(val counter: Int = 0)

sealed class MyIntent {
  object CountUp : MyIntent()
  object CountDown : MyIntent()
}

class MyViewModel : MviViewModel2<MyState, MyIntent>() {
  override val engine = engineContext {
    onInit {
      Timber.d("Hello!")
    }

    on<CountUp> {
      delay(1000)
      enterState(state.copy(counter = state.counter + 1))
    }

    streamOf<CountDown> { intents ->
      intents
          .withLatestFrom(states, BiFunction { event, state ->
            state.copy(counter = state.counter - 1)
          })
    }
  }
}
