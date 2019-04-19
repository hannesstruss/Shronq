package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.shronq.data.Weight

data class LogWeightState(
    val lastWeight: Weight,
    val seekbarValue: Int,
    val isInserting: Boolean
) {
  companion object {
    fun initial() = LogWeightState(
        lastWeight = Weight.fromGrams(0),
        seekbarValue = 50,
        isInserting = false
    )
  }

  val lastWeightText: String = String.format("%.1f", lastWeight.kilograms)

  val weightText: String
    get() {
      val seekbarRange = 1.0
      val weightDifference = ((seekbarValue - 50) / 50.0) * seekbarRange
      val value: Double = lastWeight.kilograms + weightDifference
      return String.format("%.1f", value)
    }

  val insertButtonEnabled get() = !isInserting
}
