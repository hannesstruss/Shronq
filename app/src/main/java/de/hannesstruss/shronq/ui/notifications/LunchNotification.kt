package de.hannesstruss.shronq.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.hannesstruss.kotlin.extensions.awaitFirst
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.Clock
import de.hannesstruss.shronq.data.MeasurementRepository
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class LunchNotification
@Inject constructor(
    private val context: Context,
    private val clock: Clock,
    private val measurementRepository: MeasurementRepository,
    private val lunchNotificationScheduler: LunchNotificationScheduler
) {
  companion object {
    private const val ChannelId = "lunch_infos"
    private const val NotificationId = 2
  }

  init {
    createNotificationChannel()
  }

  suspend fun triggerNow() {
    val from = clock.now().minus(14, ChronoUnit.DAYS)
    val to = clock.now().minus(7, ChronoUnit.DAYS)
    val avgWeight = measurementRepository.getAverageWeightBetween(from, to)

    if (avgWeight == null) return

    val lastWeight = measurementRepository.getLatestMeasurement().awaitFirst().weight

    val isGoingDown = lastWeight < avgWeight
    val content = if (isGoingDown) {
      NotificationText(
          title = "Keep it going!",
          text = String.format("Down from %.1f last week.", avgWeight.kilograms),
          icon = R.drawable.ic_trending_down_black_24dp
      )
    } else {
      NotificationText(
          title = "Salad time!",
          text = String.format("Up from %.1f last week. You can get back on track!", avgWeight.kilograms),
          icon = R.drawable.ic_trending_up_black_24dp
      )
    }

    val nm = NotificationManagerCompat.from(context)

    val notification = NotificationCompat.Builder(context, ChannelId)
        .setContentTitle(content.title)
        .setContentText(content.text)
        .setVibrate(listOf(500L, 500L, 500L, 500L, 500L).toLongArray())
        .setSmallIcon(content.icon)
        .setAutoCancel(true)
        .build()

    nm.notify(NotificationId, notification)

    lunchNotificationScheduler.schedule()
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

  private class NotificationText(
      val title: String,
      val text: String,
      @DrawableRes val icon: Int
  )
}
