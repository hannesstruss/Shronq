package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.android.KeyboardHider
import de.hannesstruss.kotlin.extensions.awaitFirst
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.Weight
import shronq.statemachine.StateMachineViewModel
import de.hannesstruss.shronq.ui.logweight.LogWeightIntent.LogWeight
import de.hannesstruss.shronq.ui.logweight.LogWeightIntent.Seeked
import de.hannesstruss.shronq.ui.navigation.Navigator
import de.hannesstruss.shronq.usecase.LogWeightUseCase
import javax.inject.Inject

class LogWeightViewModel
@Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val keyboardHider: KeyboardHider,
    private val logWeightUseCase: LogWeightUseCase,
    private val navigator: Navigator
) : StateMachineViewModel<LogWeightState, LogWeightIntent>() {
  override val initialState = LogWeightState.initial()

  override val engine = createEngine {
    onInit {
      val lastWeight = measurementRepository.getLatestMeasurement().awaitFirst().weight
      enterState { state.copy(lastWeight = lastWeight) }
    }

    on<Seeked> {
      enterState { state.copy(seekbarValue = it.seekedValue) }
    }

    on<LogWeight> { intent ->
      keyboardHider.hideKeyboard()

      enterState { state.copy(isInserting = true) }

      logWeightUseCase.execute(Weight.fromGrams(intent.weightGrams))

      navigator.back()
    }
  }
}
