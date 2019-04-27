package de.hannesstruss.shronq.ui.settings

import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.fit.ConnectResult
import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.navigation.Navigator
import de.hannesstruss.shronq.ui.settings.SettingsIntent.ConnectFit
import de.hannesstruss.shronq.ui.settings.SettingsIntent.DisconnectFit
import de.hannesstruss.shronq.ui.settings.SettingsIntent.GoToS3Settings
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class SettingsViewModel
@Inject constructor(
    private val fitClient: FitClient,
    private val navigator: Navigator
) : MviViewModel<SettingsState, SettingsIntent>() {
  override val initialState = SettingsState.initial()

  override val engine = createEngine {
    onInit {
      enterState { state.copy(fitIsEnabled = fitClient.isEnabled) }
    }

    on<ConnectFit> {
      if (!fitClient.isEnabled) {
        enterState { state.copy(fitIsEnabled = true) }

        val connectResult = fitClient.enable().await()
        when (connectResult) {
          ConnectResult.Success ->
            enterState { state.copy(fitIsEnabled = true) }

          ConnectResult.Canceled ->
            enterState { state.copy(fitIsEnabled = false) }

          else -> throw AssertionError("What could possibly go wrong?")
        }
      }
    }

    on<DisconnectFit> {
      if (fitClient.isEnabled) {
        fitClient.disable().await()
        enterState { state.copy(fitIsEnabled = false) }
      }
    }

    on<GoToS3Settings> {
      navigator.navigate(R.id.action_settingsFragment_to_s3SettingsFragment)
    }
  }
}
