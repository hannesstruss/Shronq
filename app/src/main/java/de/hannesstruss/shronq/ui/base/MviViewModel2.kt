package de.hannesstruss.shronq.ui.base

import de.hannesstruss.shronq.ui.base.MyIntent.CountDown
import de.hannesstruss.shronq.ui.base.MyIntent.CountUp
import io.reactivex.Observable
import kotlinx.coroutines.delay
import timber.log.Timber
import java.time.LocalTime

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
  fun <T : IntentT> streamOf(block: StreamContext<StateT>.(Observable<T>) -> Observable<StateEditor<StateT>>)
  fun externals(block: ExternalsContext<StateT>.() -> Unit)
}

interface IntentContext<StateT, IntentT> {
  fun enterState(block: StateEditorContext<StateT>.() -> StateT)
}

interface StateEditorContext<StateT> {
  val state: StateT
}

typealias StateEditor<StateT> = (StateT) -> StateT

interface StreamContext<StateT> {
  fun <T> Observable<T>.onNextEnterState(block: StateEditorContext<StateT>.(T) -> StateT): Observable<StateEditor<StateT>>
  fun <T> Observable<T>.dontEmitStates(): Observable<out StateT> = ignoreElements().toObservable()
}

interface ExternalsContext<StateT> {
  fun <T> Observable<T>.hookUp(block: suspend ExternalsItemContext<StateT>.(T) -> Unit)
}

interface ExternalsItemContext<StateT> {
  fun enterState(block: StateEditorContext<StateT>.() -> StateT)
}

data class MyState(val counter: Int = 0, val secondsSum: Int = 0) {
  fun incrementCounter() = copy(counter = counter + 1)
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

    on<CountUp> {
      delay(1000)
      enterState { state.incrementCounter() }
    }

    streamOf<CountDown> { intents ->
      intents
          .onNextEnterState {
            state.copy(counter = state.counter - 1)
          }
    }

    streamOf<CountUp> { intents ->
      intents
          .doOnNext { Timber.d("Here we're just doing side effects, for whatever reason") }
          .dontEmitStates()
    }

    externals {
      MyTimeService.times
          .map { it.second }
          .hookUp {
            enterState { state.copy(secondsSum = state.secondsSum + it) }
          }
    }
  }
}
