package de.hannesstruss.shronq.ui.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import de.hannesstruss.shronq.ui.di.PerActivity
import javax.inject.Inject
import javax.inject.Provider

@PerActivity
class ViewModelFactory @Inject constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

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