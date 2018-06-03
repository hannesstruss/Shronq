package de.hannesstruss.shronq.ui.settings

import de.hannesstruss.shronq.data.fit.ConnectResult
import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.ui.base.MviEvent.Companion.nothing
import de.hannesstruss.shronq.ui.base.MviViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val fitClient: FitClient
) : MviViewModel<SettingsState, SettingsIntent, SettingsChange, SettingsEffect>() {

  override val intentMapper = { intent: SettingsIntent ->
    when (intent) {
      SettingsIntent.ConnectFit ->
        if (!fitClient.isEnabled) {
          fitClient.enable().toObservable()
              .map {
                when (it) {
                  ConnectResult.Success -> SettingsChange.FitEnabled(true)
                  ConnectResult.Canceled -> SettingsChange.FitEnabled(false)
                  ConnectResult.Failed -> throw AssertionError("What could possibly go wrong?")
                }
              }
              .asEvents()
              .startWith(SettingsChange.FitEnabled(true).changeAsEvent())
        } else nothing()

      SettingsIntent.DisconnectFit -> if (fitClient.isEnabled) {
        fitClient.disable().andThen(SettingsChange.FitEnabled(false).changeAsEvent())
      } else nothing()
    }
  }

  override val stateReducer = { state: SettingsState, change: SettingsChange ->
    when (change) {
      is SettingsChange.FitEnabled -> state.copy(fitIsEnabled = change.enabled)
    }
  }

  override val initialState = SettingsState.initial().copy(fitIsEnabled = fitClient.isEnabled)
}
