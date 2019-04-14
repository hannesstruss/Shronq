package de.hannesstruss.shronq.ui.home

import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment2
import io.reactivex.Observable
import kotlinx.android.synthetic.main.home_fragment.btn_go_to_insert
import kotlinx.android.synthetic.main.home_fragment.btn_go_to_settings
import kotlinx.android.synthetic.main.home_fragment.btn_range_1m
import kotlinx.android.synthetic.main.home_fragment.btn_range_1y
import kotlinx.android.synthetic.main.home_fragment.btn_range_2m
import kotlinx.android.synthetic.main.home_fragment.btn_range_6m
import kotlinx.android.synthetic.main.home_fragment.btn_range_all
import kotlinx.android.synthetic.main.home_fragment.chart
import kotlinx.android.synthetic.main.home_fragment.txt_latest_weight
import java.time.Period

class HomeFragment : BaseFragment2<HomeState, HomeIntent, HomeViewModel>() {
  override val layout = R.layout.home_fragment
  override val viewModelClass = HomeViewModel::class.java

  override fun intents() = Observable.merge(listOf(
      btn_go_to_insert.clicks().map { HomeIntent.InsertWeight },
      btn_go_to_settings.clicks().map { HomeIntent.EditSettings },

      btn_range_all.clicks().map { HomeIntent.UpdateVisiblePeriod(null) },
      btn_range_1y.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(12)) },
      btn_range_6m.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(6)) },
      btn_range_2m.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(2)) },
      btn_range_1m.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(1)) }
  )).startWith(HomeIntent.Init)

  override fun render(state: HomeState) {
    chart.measurements = state.measurements
    chart.setPeriod(state.visiblePeriod)

    txt_latest_weight.text = state.latestMeasurement?.let {
      String.format("%.1f", it.weightGrams / 1000.0)
    } ?: ""
  }
}
