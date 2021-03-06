package de.hannesstruss.shronq.ui.home

import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.view.clicks
import de.hannesstruss.shronq.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.home_fragment.btn_go_to_insert
import kotlinx.android.synthetic.main.home_fragment.btn_go_to_settings
import kotlinx.android.synthetic.main.home_fragment.btn_range_1m
import kotlinx.android.synthetic.main.home_fragment.btn_range_1y
import kotlinx.android.synthetic.main.home_fragment.btn_range_2m
import kotlinx.android.synthetic.main.home_fragment.btn_range_6m
import kotlinx.android.synthetic.main.home_fragment.btn_range_all
import kotlinx.android.synthetic.main.home_fragment.chart
import kotlinx.android.synthetic.main.home_fragment.toolbar
import kotlinx.android.synthetic.main.home_fragment.txt_latest_weight
import shronq.statemachine.StateMachineFragment
import java.time.Period

class HomeFragment : StateMachineFragment<HomeState, HomeIntent, HomeViewModel>() {
  override val layout = R.layout.home_fragment
  override val viewModelClass = HomeViewModel::class.java

  override fun intents() = Observable.mergeArray(
      btn_go_to_insert.clicks().map { HomeIntent.InsertWeight },
      btn_go_to_settings.clicks().map { HomeIntent.EditSettings },

      btn_range_all.clicks().map { HomeIntent.UpdateVisiblePeriod(null) },
      btn_range_1y.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(12)) },
      btn_range_6m.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(6)) },
      btn_range_2m.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(2)) },
      btn_range_1m.clicks().map { HomeIntent.UpdateVisiblePeriod(Period.ofMonths(1)) },
      toolbar.itemClicks()
          .filter { it.itemId == R.id.show_list }
          .map { HomeIntent.ShowList }
  ).startWith(HomeIntent.Init)

  override fun render(state: HomeState) {
    chart.measurements = state.measurements
    chart.setPeriod(state.visiblePeriod)

    txt_latest_weight.text = state.latestMeasurement?.let {
      String.format("%.1f", it.weight.kilograms)
    } ?: ""
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    toolbar.inflateMenu(R.menu.home_menu)
  }
}
