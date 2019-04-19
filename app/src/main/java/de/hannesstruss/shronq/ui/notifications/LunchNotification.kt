package de.hannesstruss.shronq.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.hannesstruss.shronq.R
import javax.inject.Inject

class LunchNotification
@Inject constructor(private val context: Context) {
  companion object {
    private const val ChannelId = "lunch_infos"
    private const val NotificationId = 2
  }

  init {
    createNotificationChannel()
  }

  fun triggerNow() {
    val nm = NotificationManagerCompat.from(context)

    val notification = NotificationCompat.Builder(context, ChannelId)
        .setContentTitle("Keep it going!")
        .setContentText("Down from 102,4 last week.")
        .setSmallIcon(R.drawable.ic_trending_up_black_24dp)
        .setAutoCancel(true)
        .build()

    nm.notify(NotificationId, notification)
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Lunch Notifications"
      val description = "Lunchtime infos"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(ChannelId, name, importance).apply {
        setDescription(description)
      }

      val nm = context.getSystemService(NotificationManager::class.java)
      nm.createNotificationChannel(channel)
    }
  }
}
