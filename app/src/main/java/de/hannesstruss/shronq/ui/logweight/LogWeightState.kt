package de.hannesstruss.shronq.ui.logweight

data class LogWeightState(
    val isInserting: Boolean
) {
  companion object {
    fun initial() = LogWeightState(
        isInserting = false
    )
  }
}
