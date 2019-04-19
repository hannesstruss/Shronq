package de.hannesstruss.shronq

import android.app.Application
import com.bugsnag.android.Bugsnag
import de.hannesstruss.shronq.data.sync.SyncDownWorker
import de.hannesstruss.shronq.di.AppComponent
import de.hannesstruss.shronq.ui.notifications.LunchNotificationScheduler
import timber.log.Timber

class ShronqApp : Application() {
  val appComponent by lazy { AppComponent.init(this) }

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    } else {
      Bugsnag.init(this)
    }

    SyncDownWorker.schedulePeriodically()
    LunchNotificationScheduler(this).schedule()
  }
}
