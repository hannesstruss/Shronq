package de.hannesstruss.shronq.ui.settings

import de.hannesstruss.shronq.data.fit.ConnectResult
import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.ui.base.MviViewModel2
import de.hannesstruss.shronq.ui.settings.SettingsIntent.ConnectFit
import de.hannesstruss.shronq.ui.settings.SettingsIntent.DisconnectFit
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class SettingsViewModel2
@Inject constructor(
    private val fitClient: FitClient
) : MviViewModel2<SettingsState, SettingsIntent>() {
  init {
    println("Init SVM2")
  }

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

          ConnectResult.Failed -> throw AssertionError("What could possibly go wrong?")
        }
      }
    }

    on<DisconnectFit> {

    }
  }
}
