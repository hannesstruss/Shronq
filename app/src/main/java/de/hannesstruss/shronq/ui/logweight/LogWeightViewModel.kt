package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.ui.base.MviViewModel
import io.reactivex.Observable
import javax.inject.Inject

class LogWeightViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository
) : MviViewModel<LogWeightState, LogWeightIntent, LogWeightAction>() {
  override val actionCreator = { intent: LogWeightIntent ->
    when (intent) {
      is LogWeightIntent.LogWeight -> {
        measurementRepository.insertMeasurement(intent.weightGrams)
        Observable.empty<LogWeightAction>()
      }
    }
  }

  override val stateReducer = { state: LogWeightState, action: LogWeightAction ->
    state
  }

  override val initialState = LogWeightState.initial()
}
