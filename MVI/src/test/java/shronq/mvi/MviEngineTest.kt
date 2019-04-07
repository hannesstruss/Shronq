package shronq.mvi

import com.google.common.truth.Truth.assertThat
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineContext
import org.junit.Test
import shronq.mvi.TestIntent.CountDown
import shronq.mvi.TestIntent.CountUp

@ObsoleteCoroutinesApi
class MviEngineTest {
  val intents = PublishSubject.create<TestIntent>()

  val testCoroutineContext = TestCoroutineContext()
  val job = Job()
  val scope = CoroutineScope(testCoroutineContext + job)

  private fun engine(initializer: EngineContext<TestState, TestIntent>.() -> Unit): TestObserver<TestState> {
    val engine = MviEngine(scope, TestState.initial(), intents, initializer)
    engine.start()
    testCoroutineContext.triggerActions()
    return engine.states.test()
  }

  @Test fun `starts with initial state`() {
    val states = engine { }
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

  @Test fun `intents are unsubscribed from when job is cancelled`() {
    runBlocking {
      engine {
        on<CountUp> {
          // nothing
        }

        onFirst<CountDown> {
          // nothing
        }
      }
      assertThat(intents.hasObservers()).isTrue()
      job.cancel()
      testCoroutineContext.triggerActions()
      assertThat(intents.hasObservers()).isFalse()
    }
  }

  @Test fun `intent triggers new state`() {
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
    val e = RuntimeException("Hello")

    engine {
      on<CountUp> {
        throw e
      }
    }
    intents.onNext(TestIntent.CountUp)
    testCoroutineContext.triggerActions()

    assertThat(testCoroutineContext.exceptions).containsExactly(e)
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

  @Test fun `streamOf works`() {
    runBlocking {
      var countUpReceivedFromStreamOf = 0
      var hookUpBlockCalled = 0
      engine {
        streamOf<CountUp> { intents ->
          intents
              .doOnNext { countUpReceivedFromStreamOf++ }
              .hookUp {
                hookUpBlockCalled++
              }
        }
      }

      intents.onNext(TestIntent.CountUp)
      intents.onNext(TestIntent.CountUp)
      testCoroutineContext.triggerActions()

      assertThat(countUpReceivedFromStreamOf).isEqualTo(2)
      assertThat(hookUpBlockCalled).isEqualTo(2)
    }
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
      testCoroutineContext.triggerActions()

      states.assertValues(TestState.initial(), TestState.initial().copy(secondsSum = 2), TestState.initial().copy(secondsSum = 6))
    }
  }

  @Test fun `parameterless hookUp is unsubscribed when job is cancelled`() {
    val external = PublishSubject.create<Int>()
    engine {
      externalStream {
        external.hookUp()
      }
    }
    assertThat(external.hasObservers()).isTrue()
    job.cancel()
    testCoroutineContext.triggerActions()
    assertThat(external.hasObservers()).isFalse()
  }

  @Test fun `hookup with block is unsubscribed when job is cancelled`() {
    val external = PublishSubject.create<Int>()
    engine {
      externalStream {
        external.hookUp {
          // nothing
        }
      }
    }
    assertThat(external.hasObservers()).isTrue()
    job.cancel()
    testCoroutineContext.triggerActions()
    assertThat(external.hasObservers()).isFalse()
  }
}
