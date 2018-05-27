package de.hannesstruss.shronq.ui.home

import android.os.Bundle
import android.view.View
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable

class HomeFragment : BaseFragment<HomeState, HomeIntent, HomeViewModel>() {
  override val layout = R.layout.home_fragment
  override val viewModelClass = HomeViewModel::class.java

  lateinit var chart: HomeChart

  override val intents by lazy {
    Observable.never<HomeIntent>()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    chart = view.findViewById<HomeChart>(R.id.chart)
  }

  override fun render(state: HomeState) {
    chart.measurements = state.measurements
  }
}
