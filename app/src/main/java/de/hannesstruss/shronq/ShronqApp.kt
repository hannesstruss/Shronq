package de.hannesstruss.shronq

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.jakewharton.processphoenix.ProcessPhoenix
import de.hannesstruss.shronq.data.sync.SyncDownWorker
import de.hannesstruss.shronq.di.AppGraph
import de.hannesstruss.shronq.ui.notifications.LunchNotificationScheduler
import timber.log.Timber

class ShronqApp : Application() {
  val appComponent by lazy { AppGraph.init(this) }

  private val isMainProcess get() = !ProcessPhoenix.isPhoenixProcess(this)

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    } else {
      Bugsnag.init(this)
    }

    if (isMainProcess) {
      SyncDownWorker.schedulePeriodically()
      LunchNotificationScheduler(this).schedule()
    }
  }
}
