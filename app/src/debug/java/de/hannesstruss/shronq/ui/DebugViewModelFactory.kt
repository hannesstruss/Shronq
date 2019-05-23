package de.hannesstruss.shronq.ui

import androidx.lifecycle.ViewModel
import de.hannesstruss.shronq.ui.base.ReleaseViewModelFactory
import shronq.statemachine.ViewModelFactory
import de.hannesstruss.shronq.ui.debugdrawer.DebugDrawerViewModel
import javax.inject.Inject
import javax.inject.Provider

class DebugViewModelFactory
@Inject constructor(
    private val releaseViewModelFactory: ReleaseViewModelFactory,
    private val drawerViewModelProvider: Provider<DebugDrawerViewModel>
) : ViewModelFactory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass == DebugDrawerViewModel::class.java) {
      return drawerViewModelProvider.get() as T
    } else {
      return releaseViewModelFactory.create(modelClass)
    }
  }
}
