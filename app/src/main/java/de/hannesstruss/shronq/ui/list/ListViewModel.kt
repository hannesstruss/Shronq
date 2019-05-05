package de.hannesstruss.shronq.ui.list

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.list.ListIntent.DeleteItem
import javax.inject.Inject

class ListViewModel
@Inject constructor(
    private val measurementRepository: MeasurementRepository
) : MviViewModel<ListState, ListIntent>() {
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
