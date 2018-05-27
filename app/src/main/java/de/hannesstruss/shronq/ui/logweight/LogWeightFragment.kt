package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable

class LogWeightFragment : BaseFragment<LogWeightState, LogWeightIntent, LogWeightViewModel>() {
  override val layout = R.layout.log_weight_fragment
  override val viewModelClass = LogWeightViewModel::class.java

  override val intents = Observable.never<LogWeightIntent>()

  override fun render(state: LogWeightState) {

  }
}
