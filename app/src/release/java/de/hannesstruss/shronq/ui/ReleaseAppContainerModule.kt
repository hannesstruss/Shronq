package de.hannesstruss.shronq.ui

import dagger.Module
import dagger.Provides

@Module
class ReleaseAppContainerModule {
  @Provides fun appContainer(): AppContainer = ReleaseAppContainer()
}
