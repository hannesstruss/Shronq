package de.hannesstruss.shronq.ui.home

import java.time.Period

sealed class HomeIntent {
  object Init : HomeIntent()
  object InsertWeight : HomeIntent()
  object EditSettings : HomeIntent()
  data class UpdateVisiblePeriod(val period: Period?) : HomeIntent()
  object TestNotificaton : HomeIntent()
}
