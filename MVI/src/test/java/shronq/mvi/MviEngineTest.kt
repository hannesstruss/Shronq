package shronq.mvi

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
}
