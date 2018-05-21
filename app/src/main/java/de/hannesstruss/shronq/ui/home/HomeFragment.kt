package de.hannesstruss.shronq.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.di.AppComponent
import de.hannesstruss.shronq.ui.base.BaseFragment
import javax.inject.Inject

class HomeFragment : BaseFragment() {
  override val layout = R.layout.home_fragment

  @Inject lateinit var measurementRepository: MeasurementRepository

  override fun onInject(appComponent: AppComponent) {
    appComponent.inject(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    measurementRepository.getLatestMeasurement().subscribe { measurement ->
      view.findViewById<TextView>(R.id.txt_latest).text = String.format("%.1f", measurement.weight)
    }
  }
}
