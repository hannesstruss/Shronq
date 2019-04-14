package de.hannesstruss.shronq.ui.mvitest

import de.hannesstruss.shronq.ui.base.MviViewModel
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MviTestViewModel
@Inject constructor() : MviViewModel<MviTestState, MviTestIntent>() {

  override val initialState = MviTestState(0)

  override val engine = createEngine {
    on<MviTestIntent.Up> {
      println("Got Up")
      enterState { state.copy(loading = true) }
      delay(1000)
      enterState { state.copy(loading = false, counter = state.counter + 1) }
    }

    on<MviTestIntent.Down> {
      println("Got Down")
      enterState { state.copy(counter = state.counter - 1) }
    }

    on<MviTestIntent.Crash> {
      withContext(Dispatchers.IO) {
        throw RuntimeException("Digger")
      }
    }

    externalStream {
      Observable.interval(1, TimeUnit.SECONDS)
          .hookUp {
            enterState { state.copy(counter = state.counter + 1) }
          }
    }
  }
}
