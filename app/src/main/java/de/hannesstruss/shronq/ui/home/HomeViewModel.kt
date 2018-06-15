package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.importer.Importer
import de.hannesstruss.shronq.ui.base.MviEvent.Companion.nothing
import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.notifications.LogWeightNotification
import io.reactivex.Observable
import org.threeten.bp.LocalTime
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val importer: Importer,
    private val logWeightNotification: LogWeightNotification
) : MviViewModel<HomeState, HomeIntent, HomeChange, HomeEffect>() {

  init {
//    importer.importFirebaseToLocal()
  }

  override val intentMapper = { intent: HomeIntent ->
    when (intent) {
      HomeIntent.Init -> {
        // TODO move to settings
        logWeightNotification.enable(LocalTime.of(7, 0))
        nothing()
      }
      HomeIntent.InsertWeight -> HomeEffect.NavigateToInsertWeight.effectAsEvent()
      HomeIntent.EditSettings -> HomeEffect.NavigateToSettings.effectAsEvent()
      is HomeIntent.UpdateVisiblePeriod -> HomeChange.UpdateVisiblePeriod(intent.period).changeAsEvent()
    }
  }

  override val stateReducer = { state: HomeState, change: HomeChange ->
    when (change) {
      is HomeChange.UpdateMeasurements -> state.copy(measurements = change.measurements)
      is HomeChange.UpdateLastMeasurement -> state.copy(latestMeasurement = change.measurement)
      is HomeChange.UpdateVisiblePeriod -> state.copy(visiblePeriod = change.period)
    }
  }

  override fun extraEvents() = Observable.merge(
      measurementRepository.getMeasurements()
          .map { HomeChange.UpdateMeasurements(it) }
          .asEvents(),

      measurementRepository.getLatestMeasurement()
          .map { HomeChange.UpdateLastMeasurement(it) }
          .asEvents()
  )!!

  override val initialState = HomeState.initial()
}
