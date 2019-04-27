package de.hannesstruss.shronq.ui.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import de.hannesstruss.shronq.ui.home.HomeViewModel
import de.hannesstruss.shronq.ui.logweight.LogWeightViewModel
import de.hannesstruss.shronq.ui.s3settings.S3SettingsViewModel
import de.hannesstruss.shronq.ui.settings.SettingsViewModel
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
@Suppress("Unused")
abstract class ViewModelModule {
  @Binds @IntoMap @ViewModelKey(HomeViewModel::class)
  abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

  @Binds @IntoMap @ViewModelKey(LogWeightViewModel::class)
  abstract fun bindLogWeightViewModel(viewModel: LogWeightViewModel): ViewModel

  @Binds @IntoMap @ViewModelKey(SettingsViewModel::class)
  abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

  @Binds @IntoMap @ViewModelKey(S3SettingsViewModel::class)
  abstract fun bindS3SettingsViewModel(viewModel: S3SettingsViewModel): ViewModel
}
