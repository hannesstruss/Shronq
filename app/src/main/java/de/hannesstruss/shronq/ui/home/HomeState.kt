package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.Measurement
import java.time.Period

data class HomeState(
    val measurements: List<Measurement>,
    val latestMeasurement: Measurement?,
    val visiblePeriod: Period?
) {
  companion object {
    fun initial() = HomeState(
        measurements = emptyList(),
        latestMeasurement = null,
        visiblePeriod = null
    )
  }
}
