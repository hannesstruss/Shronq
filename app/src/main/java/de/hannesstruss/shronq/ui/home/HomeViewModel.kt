package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.importer.Importer
import de.hannesstruss.shronq.ui.base.MviViewModel
import io.reactivex.Observable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val importer: Importer
) : MviViewModel<HomeState, HomeIntent, HomeAction>() {

  init {
//    importer.import()
  }

  override val actionCreator = { intent: HomeIntent ->
    Observable.empty<HomeAction>()
  }

  override val stateReducer = { state: HomeState, action: HomeAction ->
    when (action) {
      is HomeAction.UpdateMeasurements -> state.copy(measurements = action.measurements)
      is HomeAction.UpdateLastMeasurement -> state.copy(latestMeasurement = action.measurement)
    }
  }

  override val extraActions
    get() = Observable.merge<HomeAction>(
        measurementRepository.getMeasurements()
            .map { HomeAction.UpdateMeasurements(it) },

        measurementRepository.getLatestMeasurement()
            .map { HomeAction.UpdateLastMeasurement(it) }
    )

  override val initialState = HomeState.initial()
}
