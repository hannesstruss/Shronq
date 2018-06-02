package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.importer.Importer
import de.hannesstruss.shronq.ui.base.MviEvent.Companion.nothing
import de.hannesstruss.shronq.ui.base.MviViewModel
import io.reactivex.Observable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val importer: Importer
) : MviViewModel<HomeState, HomeIntent, HomeChange, HomeEffect>() {

  init {
//    importer.import()
  }

  override val intentMapper = { intent: HomeIntent ->
    when (intent) {
      is HomeIntent.Init -> nothing()
      is HomeIntent.InsertWeight -> HomeEffect.NavigateToInsertWeight.asEvent()
    }
  }

  override val stateReducer = { state: HomeState, change: HomeChange ->
    when (change) {
      is HomeChange.UpdateMeasurements -> state.copy(measurements = change.measurements)
      is HomeChange.UpdateLastMeasurement -> state.copy(latestMeasurement = change.measurement)
    }
  }

  override val extraEvents
    get() = Observable.merge(
        measurementRepository.getMeasurements()
            .map { HomeChange.UpdateMeasurements(it) }
            .asEvents(),

        measurementRepository.getLatestMeasurement()
            .map { HomeChange.UpdateLastMeasurement(it) }
            .asEvents()
    )!!

  override val initialState = HomeState.initial()
}
