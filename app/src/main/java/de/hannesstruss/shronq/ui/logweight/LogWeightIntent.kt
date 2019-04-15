package de.hannesstruss.shronq.ui.logweight

sealed class LogWeightIntent {
  data class LogWeight(val weightGrams: Int): LogWeightIntent()
  data class Seeked(val seekedValue: Int): LogWeightIntent()
}
