package de.hannesstruss.shronq.ui.list

import de.hannesstruss.shronq.data.Measurement

data class ListState(
    val measurements: List<Measurement>
) {
  companion object {
    fun initial() = ListState(
        measurements = emptyList()
    )
  }
}
