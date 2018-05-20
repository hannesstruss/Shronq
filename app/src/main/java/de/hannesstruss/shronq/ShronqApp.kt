package de.hannesstruss.shronq

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.jakewharton.threetenabp.AndroidThreeTen

class ShronqApp : Application() {
  override fun onCreate() {
    super.onCreate()

    if (!BuildConfig.DEBUG) {
      Bugsnag.init(this)
    }

    AndroidThreeTen.init(this)
  }
}
