package de.hannesstruss.shronq

import android.app.Application
import com.bugsnag.android.Bugsnag

class ShronqApp : Application() {
  override fun onCreate() {
    super.onCreate()

    Bugsnag.init(this)
  }
}
