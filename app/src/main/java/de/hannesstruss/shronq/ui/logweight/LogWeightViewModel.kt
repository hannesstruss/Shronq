package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.android.KeyboardHider
import de.hannesstruss.kotlin.extensions.awaitFirst
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.logweight.LogWeightIntent.LogWeight
import de.hannesstruss.shronq.ui.logweight.LogWeightIntent.Seeked
import de.hannesstruss.shronq.ui.navigation.Navigator
import kotlinx.coroutines.async
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class LogWeightViewModel
@Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val fitClient: FitClient,
    private val keyboardHider: KeyboardHider,
    private val navigator: Navigator
) : MviViewModel<LogWeightState, LogWeightIntent>() {
  override val initialState = LogWeightState.initial()

  override val engine = createEngine {
    onInit {
      val lastWeight = measurementRepository.getLatestMeasurement().awaitFirst().weightGrams
      enterState { state.copy(lastWeight = lastWeight) }
    }

    on<Seeked> {
      enterState { state.copy(seekbarValue = it.seekedValue) }
    }

    on<LogWeight> { intent ->
      keyboardHider.hideKeyboard()

      enterState { state.copy(isInserting = true) }

      val insertDb = async {
        measurementRepository.insertMeasurement(intent.weightGrams)
      }

      val insertFit = async {
        if (fitClient.isEnabled) {
          fitClient.insert(intent.weightGrams).await()
        }
      }

      insertDb.join()
      insertFit.join()

      navigator.back()
    }
  }
}
