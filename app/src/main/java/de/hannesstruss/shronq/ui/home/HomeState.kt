package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.Measurement

data class HomeState(
    val measurements: List<Measurement>
) {
  companion object {
    fun initial() = HomeState(
        measurements = emptyList()
    )
  }
}
