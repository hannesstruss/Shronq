package de.hannesstruss.shronq.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

class LunchNotificationScheduler(private val context: Context) {
  companion object {
    private const val REQUEST_CODE = 3
    private val scheduledTime = LocalTime.of(12, 0)
  }

  fun schedule() {
    val intent = Intent(context, LunchNotificationPublisher::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val alarmManager = context.getSystemService(AlarmManager::class.java)

    val offset = OffsetDateTime.now().offset
    val firstOccurrence = scheduledTime.atDate(LocalDate.now().plusDays(1)).toEpochSecond(offset) * 1000
    alarmManager.cancel(pendingIntent)
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, firstOccurrence, pendingIntent)
  }
}
