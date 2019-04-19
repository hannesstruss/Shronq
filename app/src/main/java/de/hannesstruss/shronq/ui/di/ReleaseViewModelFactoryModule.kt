package de.hannesstruss.shronq.ui.di

import dagger.Binds
import dagger.Module
import de.hannesstruss.shronq.ui.base.ReleaseViewModelFactory
import de.hannesstruss.shronq.ui.base.ViewModelFactory

@Module
abstract class ReleaseViewModelFactoryModule {
  @Binds abstract fun viewModelFactory(releaseViewModelFactory: ReleaseViewModelFactory): ViewModelFactory
}
