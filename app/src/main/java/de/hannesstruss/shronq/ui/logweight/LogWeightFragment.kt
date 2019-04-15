package de.hannesstruss.shronq.ui.logweight

import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import kotlinx.android.synthetic.main.log_weight_fragment.btn_insert
import kotlinx.android.synthetic.main.log_weight_fragment.edit_weight
import kotlinx.android.synthetic.main.log_weight_fragment.txt_last_weight

class LogWeightFragment : BaseFragment<LogWeightState, LogWeightIntent, LogWeightViewModel>() {
  override val layout = R.layout.log_weight_fragment
  override val viewModelClass = LogWeightViewModel::class.java

  override fun intents() = btn_insert.clicks()
      .map<LogWeightIntent> { LogWeightIntent.LogWeight(weightGrams()) }

  override fun render(state: LogWeightState) {
    btn_insert.isEnabled = state.insertButtonEnabled
    if (state.lastWeight != null) {
      txt_last_weight.text = String.format("%.1f", state.lastWeight / 1000f)
    }
  }

  private fun weightGrams(): Int {
    return (edit_weight.text.toString().toDouble() * 1000).toInt()
  }
}
