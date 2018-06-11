package de.hannesstruss.shronq.ui.home

import org.threeten.bp.Period

sealed class HomeIntent {
  object Init : HomeIntent()
  object InsertWeight : HomeIntent()
  object EditSettings : HomeIntent()
  data class UpdateVisiblePeriod(val period: Period?) : HomeIntent()
}
