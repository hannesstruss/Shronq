package de.hannesstruss.shronq.data.sync

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import de.hannesstruss.shronq.ShronqApp
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncDownWorker : Worker() {
  companion object {
    private val CONSTRAINTS = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()

    private val WORK_NAME = "SyncDownWorker"

    fun schedulePeriodically() {
      val wm = WorkManager.getInstance()!!
      val request = PeriodicWorkRequestBuilder<SyncDownWorker>(7, TimeUnit.DAYS)
          .setConstraints(CONSTRAINTS)
          .build()
      wm.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }
  }

  @Inject lateinit var syncer: Syncer

  override fun doWork(): Worker.Result {
    (applicationContext as ShronqApp).appComponent.inject(this)

    try {
      syncer.syncDown().blockingAwait()
      return Worker.Result.SUCCESS
    } catch (e: IOException) {
      return Worker.Result.RETRY
    } catch (e: Exception) {
      return Worker.Result.FAILURE
    }
  }
}
