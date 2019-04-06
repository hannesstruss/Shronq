package de.hannesstruss.shronq.ui.mvitest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_decr
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_incr
import kotlinx.android.synthetic.main.mvi_test_fragment.txt_counter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import shronq.mvi.MviEngine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class MviTestFragment : Fragment(), CoroutineScope {
  private lateinit var job: Job

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    job = Job()
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.mvi_test_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    println("Creating engine")

    val engine = MviEngine<MviTestState, MviTestIntent>(
        coroutineScope = this,
        initialState = MviTestState(0),
        intents = Observable.merge(
            btn_incr.clicks().map { MviTestIntent.Up },
            btn_decr.clicks().map { MviTestIntent.Down }
        )
    ) {
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

      externalStream {
        Observable.interval(1, TimeUnit.SECONDS)
            .hookUp {
              enterState { state.copy(counter = state.counter + 1) }
            }
      }
    }

    engine.states
        .doOnNext { println("Got state: $it") }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          txt_counter.text = "${it.counter}"
          btn_incr.isEnabled = !it.loading
          btn_decr.isEnabled = !it.loading
        }

    engine.start()
  }

  override fun onDestroy() {
    super.onDestroy()

    job.cancel()
  }
}
