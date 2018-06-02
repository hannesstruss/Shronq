package de.hannesstruss.shronq.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.home_fragment.btn_go_to_insert
import kotlinx.android.synthetic.main.home_fragment.chart
import kotlinx.android.synthetic.main.home_fragment.txt_latest_weight

class HomeFragment : BaseFragment<HomeState, HomeIntent, HomeViewModel>() {
  override val layout = R.layout.home_fragment
  override val viewModelClass = HomeViewModel::class.java

  override val intents by lazy {
    Observable.just<HomeIntent>(HomeIntent.Init)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    btn_go_to_insert.setOnClickListener {
      findNavController().navigate(R.id.action_homeFragment_to_logWeightFragment)
    }
  }

  override fun render(state: HomeState) {
    chart.measurements = state.measurements
    txt_latest_weight.text = state.latestMeasurement?.let {
      String.format("%.1f", it.weightGrams / 1000.0)
    } ?: ""
  }
}
