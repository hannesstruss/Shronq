package de.hannesstruss.shronq.ui.logweight

sealed class LogWeightIntent {
  data class LogWeight(val weight: Int): LogWeightIntent()
}
