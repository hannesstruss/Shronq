package de.hannesstruss.shronq.ui.settings

data class SettingsState(
    val fitIsEnabled: Boolean
) {
  companion object {
    fun initial() = SettingsState(false)
  }
}
