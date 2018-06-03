package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.ui.base.MviViewModel
import javax.inject.Inject

class LogWeightViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val fitClient: FitClient
) : MviViewModel<LogWeightState, LogWeightIntent, LogWeightChange, LogWeightEffect>() {

  override val intentMapper = { intent: LogWeightIntent ->
    when (intent) {
      is LogWeightIntent.LogWeight -> {
        measurementRepository.insertMeasurement(intent.weightGrams)
        if (fitClient.isEnabled) {
          fitClient.insert(intent.weightGrams)
              .toObservable<LogWeightEffect>()
              .startWith(LogWeightEffect.GoBack)
              .effectsAsEvents()
        } else {
          LogWeightEffect.GoBack.effectAsEvent()
        }
      }
    }
  }

  override val stateReducer = { state: LogWeightState, change: LogWeightChange ->
    state
  }

  override val initialState = LogWeightState.initial()
}
