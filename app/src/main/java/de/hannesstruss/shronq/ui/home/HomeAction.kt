package de.hannesstruss.shronq.ui.home

import de.hannesstruss.shronq.data.Measurement

sealed class HomeAction {
  data class UpdateMeasurements(val measurements: List<Measurement>) : HomeAction()
}
