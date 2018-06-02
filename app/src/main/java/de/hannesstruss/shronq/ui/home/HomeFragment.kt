package de.hannesstruss.shronq.ui.home

import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.extensions.exhaust
import de.hannesstruss.shronq.ui.base.BaseFragment
import kotlinx.android.synthetic.main.home_fragment.btn_go_to_insert
import kotlinx.android.synthetic.main.home_fragment.chart
import kotlinx.android.synthetic.main.home_fragment.txt_latest_weight

class HomeFragment : BaseFragment<HomeState, HomeIntent, HomeEffect, HomeViewModel>() {
  override val layout = R.layout.home_fragment
  override val viewModelClass = HomeViewModel::class.java

  override val intents by lazy {
    btn_go_to_insert.clicks().map<HomeIntent> { HomeIntent.InsertWeight }
  }

  override fun render(state: HomeState) {
    chart.measurements = state.measurements
    txt_latest_weight.text = state.latestMeasurement?.let {
      String.format("%.1f", it.weightGrams / 1000.0)
    } ?: ""
  }

  override fun handleEffect(effect: HomeEffect) {
    when (effect) {
      HomeEffect.NavigateToInsertWeight -> findNavController().navigate(R.id.action_homeFragment_to_logWeightFragment)
    }.exhaust()
  }
}
