package de.hannesstruss.shronq.ui.settings

import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.ui.base.MviViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val fitClient: FitClient
) : MviViewModel<SettingsState, SettingsIntent, SettingsChange, SettingsEffect>() {

  override val intentMapper = { intent: SettingsIntent ->
    when (intent) {
      SettingsIntent.Connect -> fitClient.connect().asNoEvent()
    }
  }

  override val stateReducer = { state: SettingsState, change: SettingsChange ->
    state
  }

  override val initialState = SettingsState.initial()
}
