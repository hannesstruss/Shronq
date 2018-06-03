package de.hannesstruss.shronq.ui.home

sealed class HomeEffect {
  object NavigateToInsertWeight : HomeEffect()
  object NavigateToSettings : HomeEffect()
}
