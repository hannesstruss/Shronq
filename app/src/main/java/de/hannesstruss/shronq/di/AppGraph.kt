package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.amazonaws.services.s3.AmazonS3Client
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.data.Clock
import de.hannesstruss.shronq.data.db.AppDatabase
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.s3sync.BackupToS3Worker
import de.hannesstruss.shronq.ui.AppContainer
import de.hannesstruss.shronq.ui.notifications.LunchNotificationPublisher
import de.hannesstruss.shronq.widget.ShronqWidgetProvider

interface AppGraph {
  companion object {
    fun init(app: Application): AppGraph {
      return DaggerAppComponent.builder()
          .appModule(AppModule(app))
          .build()
    }

    fun get(context: Context): AppGraph {
      return (context.applicationContext as ShronqApp).appComponent
    }
  }

  fun appContainer(): AppContainer
  fun dbMeasurementDao(): DbMeasurementDao
  fun clock(): Clock
  fun context(): Context
  fun widgetUpdater(): ShronqWidgetProvider.Updater
  fun sharedPreferences(): SharedPreferences
  fun amazonS3Client(): AmazonS3Client
  fun appDatabase(): AppDatabase

  fun inject(backupToS3Worker: BackupToS3Worker)
  fun inject(lunchNotificationPublisher: LunchNotificationPublisher)
  fun inject(shronqWidgetProvider: ShronqWidgetProvider)
}
