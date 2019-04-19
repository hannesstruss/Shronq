package de.hannesstruss.shronq.ui

import dagger.Module
import dagger.Provides

@Module
class DebugAppContainerModule {
  @Provides fun appContainer(): AppContainer {
    return DebugAppContainer()
  }
}
