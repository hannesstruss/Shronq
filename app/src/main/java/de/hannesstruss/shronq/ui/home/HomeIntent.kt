package de.hannesstruss.shronq.ui.home

import java.time.Period

sealed class HomeIntent {
  object Init : HomeIntent()
  object InsertWeight : HomeIntent()
  object EditSettings : HomeIntent()
  object ShowList : HomeIntent()
  data class UpdateVisiblePeriod(val period: Period?) : HomeIntent()
}
