package de.hannesstruss.shronq.ui.di

import dagger.Subcomponent

@Subcomponent(
    modules = [
      ActivityModule::class,
      DebugViewModelModule::class
    ]
)
@PerActivity
interface ActivityComponent : ActivityGraph {
  @Subcomponent.Builder
  interface Builder {
    fun activityModule(activityModule: ActivityModule): Builder
    fun build(): ActivityComponent
  }
}
