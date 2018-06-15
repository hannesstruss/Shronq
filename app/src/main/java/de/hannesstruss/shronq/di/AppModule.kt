package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import de.hannesstruss.shronq.data.DataModule

@Module(
    includes = [
      DataModule::class
    ]
)
class AppModule(private val app: Application) {
  @Provides fun context(): Context = app
}
