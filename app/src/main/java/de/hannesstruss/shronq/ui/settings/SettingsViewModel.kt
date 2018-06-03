package de.hannesstruss.shronq.ui.settings

import de.hannesstruss.shronq.ui.base.MviEvent.Companion.nothing
import de.hannesstruss.shronq.ui.base.MviViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : MviViewModel<SettingsState, SettingsIntent, SettingsChange, SettingsEffect>() {
  override val intentMapper = { intent: SettingsIntent ->
    nothing()
  }

  override val stateReducer = { state: SettingsState, change: SettingsChange ->
    state
  }

  override val initialState = SettingsState.initial()
}
