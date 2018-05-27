package de.hannesstruss.shronq.ui.logweight

import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import kotlinx.android.synthetic.main.log_weight_fragment.btn_insert
import kotlinx.android.synthetic.main.log_weight_fragment.edit_weight

class LogWeightFragment : BaseFragment<LogWeightState, LogWeightIntent, LogWeightViewModel>() {
  override val layout = R.layout.log_weight_fragment
  override val viewModelClass = LogWeightViewModel::class.java

  override val intents get() = btn_insert.clicks()
      .map<LogWeightIntent> { LogWeightIntent.LogWeight(weightGrams()) }

  override fun render(state: LogWeightState) {

  }

  private fun weightGrams(): Int {
    return (edit_weight.text.toString().toDouble() * 1000).toInt()
  }
}
