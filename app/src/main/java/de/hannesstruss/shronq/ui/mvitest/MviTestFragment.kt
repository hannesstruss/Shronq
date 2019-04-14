package de.hannesstruss.shronq.ui.mvitest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_crash
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_decr
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_incr
import kotlinx.android.synthetic.main.mvi_test_fragment.txt_counter

class MviTestFragment : Fragment() {
  private var statesDisposable: Disposable? = null
  lateinit var viewModel: MviTestViewModel

  override fun onAttach(context: Context?) {
    super.onAttach(context)

    viewModel = ViewModelProviders.of(this).get(MviTestViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.mvi_test_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.attachView(Observable.merge(
        btn_incr.clicks().map { MviTestIntent.Up },
        btn_decr.clicks().map { MviTestIntent.Down },
        btn_crash.clicks().map { MviTestIntent.Crash }
    ))

    statesDisposable = viewModel.states
        .doOnNext { println("Got state: $it") }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          txt_counter.text = "${it.counter}"
          btn_incr.isEnabled = !it.loading
          btn_decr.isEnabled = !it.loading
        }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    statesDisposable?.dispose()
    viewModel.dropView()
  }
}
