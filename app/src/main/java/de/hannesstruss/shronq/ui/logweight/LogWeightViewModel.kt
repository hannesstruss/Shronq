package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.ui.base.MviEvent
import de.hannesstruss.shronq.ui.base.MviViewModel
import io.reactivex.Observable
import javax.inject.Inject

class LogWeightViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val fitClient: FitClient
) : MviViewModel<LogWeightState, LogWeightIntent, LogWeightChange, LogWeightEffect>() {

  override val intentMapper = { intent: LogWeightIntent ->
    when (intent) {
      is LogWeightIntent.LogWeight -> {
        val o: Observable<MviEvent<out LogWeightChange, out LogWeightEffect>> = Observable.merge(
            measurementRepository.insertMeasurement(intent.weightGrams).toObservable(),

            if (fitClient.isEnabled) {
              fitClient.insert(intent.weightGrams).toObservable()
            } else {
              Observable.empty<MviEvent<LogWeightChange, LogWeightEffect>>()
            }
        )
        o.concatWith(LogWeightEffect.GoBack.effectAsEvent())
      }
    }
  }

  override val stateReducer = { state: LogWeightState, change: LogWeightChange ->
    state
  }

  override val initialState = LogWeightState.initial()
}
