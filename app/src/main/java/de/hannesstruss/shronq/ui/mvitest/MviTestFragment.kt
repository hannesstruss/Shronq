package de.hannesstruss.shronq.ui.mvitest

import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment2
import io.reactivex.Observable
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_crash
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_decr
import kotlinx.android.synthetic.main.mvi_test_fragment.btn_incr
import kotlinx.android.synthetic.main.mvi_test_fragment.txt_counter

class MviTestFragment : BaseFragment2<MviTestState, MviTestIntent, MviTestViewModel>() {
  override val layout = R.layout.mvi_test_fragment
  override val viewModelClass = MviTestViewModel::class.java

  override fun intents() = Observable.merge(
      btn_incr.clicks().map { MviTestIntent.Up },
      btn_decr.clicks().map { MviTestIntent.Down },
      btn_crash.clicks().map { MviTestIntent.Crash }
  )

  override fun render(state: MviTestState) {
    txt_counter.text = "${state.counter}"
    btn_incr.isEnabled = !state.loading
    btn_decr.isEnabled = !state.loading
  }
}
