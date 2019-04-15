package de.hannesstruss.shronq.ui.logweight

data class LogWeightState(
    val lastWeight: Int?,
    val isInserting: Boolean
) {
  companion object {
    fun initial() = LogWeightState(
        lastWeight = null,
        isInserting = false
    )
  }

  val insertButtonEnabled get() = !isInserting
}
