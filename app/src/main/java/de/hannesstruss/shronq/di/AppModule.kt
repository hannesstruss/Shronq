package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import de.hannesstruss.shronq.data.Clock
import de.hannesstruss.shronq.data.DataModule
import java.time.Instant
import java.time.ZonedDateTime

@Module(
    includes = [
      DataModule::class
    ]
)
class AppModule(private val app: Application) {
  @Provides fun context(): Context = app

  @Provides fun clock(): Clock = object : Clock {
    override fun now(): Instant {
      return Instant.now()
    }

    override fun nowWithZone(): ZonedDateTime {
      return ZonedDateTime.now()
    }
  }
}
