package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.Measurement
import java.time.Period

sealed class HomeChange {
  data class UpdateMeasurements(val measurements: List<Measurement>) : HomeChange()
  data class UpdateLastMeasurement(val measurement: Measurement) : HomeChange()
  data class UpdateVisiblePeriod(val period: Period?) : HomeChange()
}
