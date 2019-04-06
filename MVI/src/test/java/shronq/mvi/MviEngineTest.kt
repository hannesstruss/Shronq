package shronq.mvi

import com.google.common.truth.Truth.assertThat
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineContext
import org.junit.Before
import org.junit.Test
import shronq.mvi.TestIntent.CountDown
import shronq.mvi.TestIntent.CountUp
import java.util.concurrent.TimeUnit

class MviEngineTest {
  val intents = PublishSubject.create<TestIntent>()

  val testCoroutineContext = TestCoroutineContext()
  val scope = object : CoroutineScope {
    override val coroutineContext = testCoroutineContext
  }

  val times = PublishSubject.create<Int>()
  var countUpReceivedFromStreamOf = 0
  var countUpReceivedFromOnFirst = 0

  val engine: MviEngine<TestState, TestIntent> = MviEngine(
      scope,
      TestState.initial(),
      intents
  ) {
    onInit {
      println("Hello!")
      delay(200)
      println("Hello again!")
    }

    // TODO: nested view model engines
    // nest(myNestedEngine) { nestedState -> ... }

    on<CountUp> {
      enterState { state.incrementCounter() }
    }

    onFirst<CountUp> {
      countUpReceivedFromOnFirst++
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
          .doOnNext { countUpReceivedFromStreamOf++ }
          .hookUp()
    }

    externalStream {
      times
          .map { it * 2 }
          .hookUp {
            enterState { state.addSeconds(it) }
          }
    }
  }

  val states = engine.states.test()

  @Before fun setup() {
    engine.start()
  }

  @Test fun `starts with initial state`() {
    states.assertValues(TestState.initial())
  }

  @Test fun `intention triggers new state`() {
    runBlocking {
      intents.onNext(TestIntent.CountUp)
      testCoroutineContext.triggerActions()
      states.assertValues(TestState.initial(), TestState(counter = 1))
    }
  }

  @Test fun `onFirst works`() {
    runBlocking {
      intents.onNext(TestIntent.CountUp)
      intents.onNext(TestIntent.CountUp)
      testCoroutineContext.triggerActions()
      assertThat(countUpReceivedFromOnFirst).isEqualTo(1)
    }
  }

  @Test fun `streamOf works`() {
    runBlocking {
      intents.onNext(TestIntent.CountUp)
      intents.onNext(TestIntent.CountUp)
      testCoroutineContext.triggerActions()
      assertThat(countUpReceivedFromStreamOf).isEqualTo(2)
    }
  }

  @Test fun `externals work`() {
    runBlocking {
      times.onNext(1)
      times.onNext(2)
      states.assertValues(TestState.initial(), TestState.initial().copy(secondsSum = 2), TestState.initial().copy(secondsSum = 6))
    }
  }
}
