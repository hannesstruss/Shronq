package de.hannesstruss.shronq.ui.mvitest

sealed class MviTestIntent {
  object Up : MviTestIntent()
  object Down : MviTestIntent()
  object Crash : MviTestIntent()
}
