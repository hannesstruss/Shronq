package de.hannesstruss.shronq.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.hannesstruss.shronq.di.AppGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LunchNotificationPublisher : BroadcastReceiver() {
  @Inject lateinit var lunchNotification: LunchNotification
  private val scope = CoroutineScope(Dispatchers.Main)

  override fun onReceive(context: Context, intent: Intent?) {
    AppGraph.get(context).inject(this)

    scope.launch {
      lunchNotification.triggerNow()
    }
  }
}
