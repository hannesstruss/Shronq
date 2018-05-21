package de.hannesstruss.shronq

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

class ShronqApp : Application() {
  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    } else {
      Bugsnag.init(this)
    }

    AndroidThreeTen.init(this)
  }
}
