package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import de.hannesstruss.shronq.data.Clock
import de.hannesstruss.shronq.data.DataModule
import de.hannesstruss.shronq.widget.ShronqWidgetProvider
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Singleton

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

  @Provides fun widgetUpdater(context: Context): ShronqWidgetProvider.Updater =
      ShronqWidgetProvider.RealUpdater(context)

  @Provides @Singleton fun sharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("shronq.prefs", Context.MODE_PRIVATE)
  }
}
