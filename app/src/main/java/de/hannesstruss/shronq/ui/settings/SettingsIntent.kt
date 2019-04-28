package de.hannesstruss.shronq.ui.settings

sealed class SettingsIntent {
  object ConnectFit : SettingsIntent()
  object DisconnectFit : SettingsIntent()
  object GoToS3Settings : SettingsIntent()
}
