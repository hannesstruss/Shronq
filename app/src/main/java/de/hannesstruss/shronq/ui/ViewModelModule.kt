package de.hannesstruss.shronq.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import de.hannesstruss.shronq.ui.base.ViewModelFactory
import de.hannesstruss.shronq.ui.home.HomeViewModel
import de.hannesstruss.shronq.ui.logweight.LogWeightViewModel
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

  @Binds abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

  @Binds @IntoMap @ViewModelKey(HomeViewModel::class)
  abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

  @Binds @IntoMap @ViewModelKey(LogWeightViewModel::class)
  abstract fun bindLogWeightViewModel(viewModel: LogWeightViewModel): ViewModel
}
