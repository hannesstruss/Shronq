package de.hannesstruss.shronq.di

import dagger.Component
import de.hannesstruss.shronq.ui.DebugAppContainerModule
import javax.inject.Singleton

@Component(
    modules = [
      AppModule::class,
      DebugAppContainerModule::class
    ]
)
@Singleton
interface AppComponent : AppGraph, DebugAppGraph
