package de.hannesstruss.shronq.ui.base

import androidx.lifecycle.ViewModel
import de.hannesstruss.shronq.ui.di.PerActivity
import shronq.statemachine.ViewModelFactory
import javax.inject.Inject
import javax.inject.Provider

@PerActivity
class ReleaseViewModelFactory
@Inject constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelFactory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    val provider = viewModels[modelClass]
    if (provider != null) {
      return provider.get() as T
    } else {
      throw IllegalStateException("No provider available for ${modelClass.simpleName}")
    }
  }
}
