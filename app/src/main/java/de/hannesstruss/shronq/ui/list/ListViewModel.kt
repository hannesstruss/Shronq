package de.hannesstruss.shronq.ui.list

import de.hannesstruss.shronq.data.MeasurementRepository
import shronq.statemachine.StateMachineViewModel
import de.hannesstruss.shronq.ui.list.ListIntent.DeleteItem
import javax.inject.Inject

class ListViewModel
@Inject constructor(
    private val measurementRepository: MeasurementRepository
) : StateMachineViewModel<ListState, ListIntent>() {
  override val initialState = ListState.initial()

  override val engine = createEngine {
    on<DeleteItem> {
      measurementRepository.deleteById(it.id)
    }

    externalFlow {
      measurementRepository.getMeasurementsNewestFirst()
          .hookUp {
            enterState { state.copy(measurements = it) }
          }
    }
  }
}
