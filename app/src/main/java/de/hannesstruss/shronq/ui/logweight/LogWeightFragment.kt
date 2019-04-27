package de.hannesstruss.shronq.ui.logweight

import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.changes
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.log_weight_fragment.btn_insert
import kotlinx.android.synthetic.main.log_weight_fragment.edit_weight
import kotlinx.android.synthetic.main.log_weight_fragment.seekbar
import kotlinx.android.synthetic.main.log_weight_fragment.txt_last_weight

class LogWeightFragment : BaseFragment<LogWeightState, LogWeightIntent, LogWeightViewModel>() {
  override val layout = R.layout.log_weight_fragment
  override val viewModelClass = LogWeightViewModel::class.java

  override fun intents() = Observable.merge(
      btn_insert.clicks().map { LogWeightIntent.LogWeight(weightGrams()) },
      seekbar.changes().map { LogWeightIntent.Seeked(it) }
  )

  override fun render(state: LogWeightState) {
    btn_insert.isEnabled = state.insertButtonEnabled
    txt_last_weight.text = state.lastWeightText

    edit_weight.setText(state.weightText)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    seekbar.min = 0
    seekbar.max = 100
    seekbar.setProgress(50, false)
  }

  private fun weightGrams(): Int {
    return (edit_weight.text.toString().toDouble() * 1000).toInt()
  }
}
