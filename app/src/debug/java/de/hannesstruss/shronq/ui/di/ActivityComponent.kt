package de.hannesstruss.shronq.ui.di

import dagger.Component
import de.hannesstruss.shronq.di.AppGraph

@Component(
    modules = [
      ActivityModule::class,
      DebugViewModelModule::class
    ],
    dependencies = [AppGraph::class]
)
@PerActivity
interface ActivityComponent : ActivityGraph
