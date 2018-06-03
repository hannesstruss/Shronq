package de.hannesstruss.shronq.ui.settings

sealed class SettingsChange {
  data class FitEnabled(val enabled: Boolean) : SettingsChange()
}
