package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.ui.base.MviViewModel
import io.reactivex.Observable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository
) : MviViewModel<HomeState, HomeIntent, HomeAction>() {

  override val actionCreator = { intent: HomeIntent ->
    Observable.empty<HomeAction>()
  }

  override val stateReducer = { state: HomeState, action: HomeAction ->
    when (action) {
      is HomeAction.UpdateMeasurements -> state.copy(measurements = action.measurements)
    }
  }

  override val extraActions
    get() = measurementRepository.getMeasurements()
        .map<HomeAction> { HomeAction.UpdateMeasurements(it) }

  override val initialState = HomeState.initial()
}
