package de.hannesstruss.shronq.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LogWeightNotificationPublisher : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    LogWeightNotification(context).triggerNow()
  }
}
