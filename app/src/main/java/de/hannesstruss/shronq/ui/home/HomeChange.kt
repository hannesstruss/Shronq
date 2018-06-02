package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.Measurement

sealed class HomeChange {
  data class UpdateMeasurements(val measurements: List<Measurement>) : HomeChange()
  data class UpdateLastMeasurement(val measurement: Measurement): HomeChange()
}
