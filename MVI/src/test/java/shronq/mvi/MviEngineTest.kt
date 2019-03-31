package shronq.mvi

import com.google.common.truth.Truth.assertThat
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.delay
import org.junit.Test
import shronq.mvi.MviEngine.Companion.create
import shronq.mvi.TestIntent.CountDown
import shronq.mvi.TestIntent.CountUp
import java.util.concurrent.TimeUnit

class MviEngineTest {
  val intents = PublishSubject.create<TestIntent>()

  val engine: MviEngine<TestState, TestIntent> = create(TestState.initial(), intents) {
    onInit {
      println("Hello!")
      delay(200)
      println("Hello again!")
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
          .doOnNext { println("Here we're just doing side effects, for whatever reason") }
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

  val states = engine.states.test()

  @Test fun `starts with initial state`() {
    assertThat(states.events.first()).isEqualTo(TestState.initial())
  }
}
