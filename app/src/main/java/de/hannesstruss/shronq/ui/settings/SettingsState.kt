package de.hannesstruss.shronq.ui.settings

data class SettingsState(
    val x: Boolean
) {
  companion object {
    fun initial() = SettingsState(false)
  }
}
