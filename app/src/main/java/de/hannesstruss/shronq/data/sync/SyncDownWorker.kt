package de.hannesstruss.shronq.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import de.hannesstruss.shronq.ShronqApp
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncDownWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
  companion object {
    private val CONSTRAINTS = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()

    private const val WORK_NAME = "SyncDownWorker"

    fun schedulePeriodically() {
      val wm = WorkManager.getInstance()

      val request = PeriodicWorkRequestBuilder<SyncDownWorker>(7, TimeUnit.DAYS)
          .setConstraints(CONSTRAINTS)
          .build()
      wm.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }
  }

  @Inject lateinit var syncer: Syncer

  override fun doWork(): Result {
    (applicationContext as ShronqApp).appComponent.inject(this)

    return try {
      syncer.syncDown().blockingAwait()
      Result.success()
    } catch (e: IOException) {
      Result.retry()
    } catch (e: Exception) {
      Result.failure()
    }
  }
}
