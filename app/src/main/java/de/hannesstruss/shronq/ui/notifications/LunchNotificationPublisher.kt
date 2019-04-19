package de.hannesstruss.shronq.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.hannesstruss.shronq.di.AppComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LunchNotificationPublisher : BroadcastReceiver() {
  @Inject lateinit var lunchNotification: LunchNotification

  override fun onReceive(context: Context, intent: Intent?) {
    AppComponent.get(context).inject(this)

    GlobalScope.launch {
      lunchNotification.triggerNow()
    }
  }
}
