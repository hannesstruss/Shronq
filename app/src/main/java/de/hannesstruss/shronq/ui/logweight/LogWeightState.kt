package de.hannesstruss.shronq.ui.logweight

data class LogWeightState(
    val lastWeight: Int,
    val seekbarValue: Int,
    val isInserting: Boolean
) {
  companion object {
    fun initial() = LogWeightState(
        lastWeight = 0,
        seekbarValue = 50,
        isInserting = false
    )
  }

  val lastWeightText: String = String.format("%.1f", lastWeight / 1000.0)

  val weightText: String
    get() {
      val seekbarRange = 1.0
      val weightDifference = ((seekbarValue - 50) / 50.0) * seekbarRange
      val value: Double = lastWeight / 1000.0 + weightDifference
      return String.format("%.1f", value)
    }

  val insertButtonEnabled get() = !isInserting
}
