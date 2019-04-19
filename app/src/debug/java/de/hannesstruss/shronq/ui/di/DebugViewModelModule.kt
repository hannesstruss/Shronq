package de.hannesstruss.shronq.ui.di

import dagger.Binds
import dagger.Module
import de.hannesstruss.shronq.ui.DebugViewModelFactory
import de.hannesstruss.shronq.ui.base.ViewModelFactory

@Module(
    includes = [ViewModelModule::class]
)
abstract class DebugViewModelModule {
  @Binds abstract fun viewModelFactory(debugViewModelFactory: DebugViewModelFactory): ViewModelFactory
}
