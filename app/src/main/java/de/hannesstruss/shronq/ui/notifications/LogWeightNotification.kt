package de.hannesstruss.shronq.ui.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import de.hannesstruss.shronq.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LogWeightNotification @Inject constructor(private val context: Context) {
  companion object {
    private const val LogWeightChannelId = "log_weight"
    private const val LogWeightNotificationId = 1
  }

  init {
    createNotificationChannels()
  }

  fun enable(time: LocalTime) {
    val intent = Intent(context, LogWeightNotificationPublisher::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val offset = OffsetDateTime.now().offset
    val firstOccurrence = time.atDate(LocalDate.now().plusDays(1)).toEpochSecond(offset) * 1000
    alarmManager.setRepeating(AlarmManager.RTC, firstOccurrence, TimeUnit.DAYS.toMillis(1), pendingIntent)
  }

  fun triggerNow() {
    val nm = NotificationManagerCompat.from(context)

    val pendingIntent = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.logWeightFragment)
        .createPendingIntent()

    val notification = NotificationCompat.Builder(context, LogWeightChannelId)
        .setContentTitle("Hey! Tracking time.")
        .setContentText("How is it looking today?")
        .setSmallIcon(R.drawable.ic_trending_up_black_24dp)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
    nm.notify(LogWeightNotificationId, notification)
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Log Weight Notifications"
      val description = "Nagging you to log your weight"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(LogWeightChannelId, name, importance).apply {
        setDescription(description)
      }

      val nm = context.getSystemService(NotificationManager::class.java)
      nm.createNotificationChannel(channel)
    }

  }
}
