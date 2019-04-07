package shronq.mvi

import com.google.common.truth.Truth.assertThat
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineContext
import org.junit.Test
import shronq.mvi.TestIntent.CountUp

class MviEngineTest {
  val intents = PublishSubject.create<TestIntent>()

  val testCoroutineContext = TestCoroutineContext()
  val scope = object : CoroutineScope {
    override val coroutineContext = testCoroutineContext
  }

  private fun engine(initializer: EngineContext<TestState, TestIntent>.() -> Unit): TestObserver<TestState> {
    val engine = MviEngine(scope, TestState.initial(), intents, initializer)
    engine.start()
    return engine.states.test()
  }

  @Test fun `starts with initial state`() {
    val states = engine {  }
    states.assertValues(TestState.initial())
  }

  @Test fun `calls onInit`() {
    var onInitCalled = 0
    engine {
      onInit {
        onInitCalled++
      }
    }
    testCoroutineContext.triggerActions()
    assertThat(onInitCalled).isEqualTo(1)
  }

  @Test fun `errors from onInit are relayed`() {
    TODO("Not implemented")
  }

  @Test fun `intention triggers new state`() {
    runBlocking {
      val states = engine {
        on<CountUp> {
          enterState { state.incrementCounter() }
        }
      }
      intents.onNext(TestIntent.CountUp)
      testCoroutineContext.triggerActions()
      states.assertValues(TestState.initial(), TestState(counter = 1))
    }
  }

  @Test fun `errors from intention handlers are relayed`() {
    TODO("Not implemented")
  }

  @Test fun `onFirst works`() {
    runBlocking {
      var countUpReceivedFromOnFirst = 0
      engine {
        onFirst<CountUp> {
          countUpReceivedFromOnFirst++
        }
      }
      intents.onNext(TestIntent.CountUp)
      intents.onNext(TestIntent.CountUp)
      testCoroutineContext.triggerActions()
      assertThat(countUpReceivedFromOnFirst).isEqualTo(1)
    }
  }

  @Test fun `errors from onFirst are relayed`() {
    TODO("Not implemented")
  }

  @Test fun `streamOf works`() {
    runBlocking {
      var countUpReceivedFromStreamOf = 0
      engine {
        streamOf<CountUp> { intents ->
          intents
              .doOnNext { countUpReceivedFromStreamOf++ }
              .hookUp()
        }
      }

      intents.onNext(TestIntent.CountUp)
      intents.onNext(TestIntent.CountUp)
      testCoroutineContext.triggerActions()
      assertThat(countUpReceivedFromStreamOf).isEqualTo(2)
    }
  }

  @Test fun `errors from streamOf handlers are relayed`() {
    TODO("Not implemented")
  }

  @Test fun `errors from streamOf observables are relayed`() {
    TODO("Not implemented")
  }

  @Test fun `externals work`() {
    runBlocking {
      val times = PublishSubject.create<Int>()

      val states = engine {
        externalStream {
          times
              .map { it * 2 }
              .hookUp {
                enterState { state.addSeconds(it) }
              }
        }
      }

      times.onNext(1)
      times.onNext(2)
      states.assertValues(TestState.initial(), TestState.initial().copy(secondsSum = 2), TestState.initial().copy(secondsSum = 6))
    }
  }

  @Test fun `errors from external stream handlers are relayed`() {
    TODO("Not implemented")
  }

  @Test fun `errors from external streams are relayed`() {
    TODO("Not implemented")
  }

  @Test fun `throws when starting twice`() {
    TODO("Not implemented")
  }

  @Test fun `can be disposed`() {
    TODO("Not implemented")
  }
}
