package de.hannesstruss.shronq

import android.app.Application
import com.bugsnag.android.Bugsnag
import de.hannesstruss.shronq.data.sync.SyncDownWorker
import de.hannesstruss.shronq.di.AppComponent
import timber.log.Timber

class ShronqApp : Application() {
  lateinit var appComponent: AppComponent

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    } else {
      Bugsnag.init(this)
    }

    appComponent = AppComponent.init(this)

    SyncDownWorker.schedulePeriodically()
  }
}
