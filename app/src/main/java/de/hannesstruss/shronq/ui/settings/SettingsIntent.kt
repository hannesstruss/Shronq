package de.hannesstruss.shronq.ui.settings

sealed class SettingsIntent {
  object Connect : SettingsIntent()
}
