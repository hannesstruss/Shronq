package de.hannesstruss.shronq.ui.home

sealed class HomeIntent {
  object Init : HomeIntent()
  object InsertWeight : HomeIntent()
  object EditSettings : HomeIntent()
}
