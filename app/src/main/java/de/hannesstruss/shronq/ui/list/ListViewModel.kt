package de.hannesstruss.shronq.ui.list

import de.hannesstruss.kotlin.extensions.awaitFirst
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.ui.base.MviViewModel
import javax.inject.Inject

class ListViewModel
@Inject constructor(
    private val measurementRepository: MeasurementRepository
) : MviViewModel<ListState, ListIntent>() {
  override val initialState = ListState.initial()

  override val engine = createEngine {
    onInit {
      val all = measurementRepository.getMeasurementsNewestFirst().awaitFirst()
      enterState { state.copy(measurements = all) }
    }
  }
}
