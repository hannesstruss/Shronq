package de.hannesstruss.shronq.ui.di

import dagger.Subcomponent
import de.hannesstruss.shronq.ui.base.ViewModelFactory

@Subcomponent(
    modules = [
      ActivityModule::class,
      ViewModelModule::class
    ]
)
@PerActivity
interface ActivityComponent {
  @Subcomponent.Builder
  interface Builder {
    fun activityModule(activityModule: ActivityModule): Builder
    fun build(): ActivityComponent
  }

  fun viewModelFactory(): ViewModelFactory
}
