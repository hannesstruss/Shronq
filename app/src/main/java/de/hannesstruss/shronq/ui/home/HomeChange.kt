package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.Measurement
import org.threeten.bp.Period

sealed class HomeChange {
  data class UpdateMeasurements(val measurements: List<Measurement>) : HomeChange()
  data class UpdateLastMeasurement(val measurement: Measurement): HomeChange()
  data class UpdateVisiblePeriod(val period: Period?) : HomeChange()
}
