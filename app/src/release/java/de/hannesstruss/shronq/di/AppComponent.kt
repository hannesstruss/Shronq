package de.hannesstruss.shronq.di

import dagger.Component
import de.hannesstruss.shronq.ui.ReleaseAppContainerModule
import javax.inject.Singleton

@Component(
    modules = [
      AppModule::class,
      ReleaseAppContainerModule::class
    ]
)
@Singleton
interface AppComponent : AppGraph
