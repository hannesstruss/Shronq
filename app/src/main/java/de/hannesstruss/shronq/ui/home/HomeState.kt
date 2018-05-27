package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.Measurement

data class HomeState(
    val measurements: List<Measurement>,
    val latestMeasurement: Measurement?
) {
  companion object {
    fun initial() = HomeState(
        measurements = emptyList(),
        latestMeasurement = null
    )
  }
}
