package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.home.HomeIntent.EditSettings
import de.hannesstruss.shronq.ui.home.HomeIntent.InsertWeight
import de.hannesstruss.shronq.ui.home.HomeIntent.ShowList
import de.hannesstruss.shronq.ui.home.HomeIntent.UpdateVisiblePeriod
import de.hannesstruss.shronq.ui.navigation.Navigator
import de.hannesstruss.shronq.ui.notifications.LogWeightNotification
import java.time.LocalTime
import javax.inject.Inject

class HomeViewModel
@Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val logWeightNotification: LogWeightNotification,
    private val navigator: Navigator
) : MviViewModel<HomeState, HomeIntent>() {
  override val initialState = HomeState.initial()

  override val engine = createEngine {
    onInit {
      // TODO: that shouldn't happen here
      logWeightNotification.enable(LocalTime.of(7, 0))
    }

    on<InsertWeight> {
      navigator.navigate(R.id.action_homeFragment_to_logWeightFragment)
    }

    on<EditSettings> {
      navigator.navigate(R.id.action_homeFragment_to_settingsFragment)
    }

    on<UpdateVisiblePeriod> {
      enterState { state.copy(visiblePeriod = it.period) }
    }

    on<ShowList> {
      navigator.navigate(R.id.action_homeFragment_to_listFragment)
    }

    externalFlow {
      measurementRepository.getMeasurements()
          .hookUp {
            enterState { state.copy(measurements = it) }
          }
    }

    externalFlow {
      measurementRepository.getLatestMeasurement()
          .hookUp {
            enterState { state.copy(latestMeasurement = it) }
          }
    }
  }
}
