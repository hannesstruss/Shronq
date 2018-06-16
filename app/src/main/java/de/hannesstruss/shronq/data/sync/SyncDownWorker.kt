package de.hannesstruss.shronq.data.sync

import android.arch.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkStatus
import androidx.work.Worker
import de.hannesstruss.shronq.ShronqApp
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncDownWorker : Worker() {
  companion object {
    private val TAG = SyncDownWorker::class.java.simpleName

    private val CONSTRAINTS = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()

    fun schedulePeriodically() {
      val wm = WorkManager.getInstance()

      val isAlreadyScheduledObserver = object : Observer<List<WorkStatus>> {
        override fun onChanged(t: List<WorkStatus>?) {
          wm.getStatusesByTag(TAG).removeObserver(this)

          if (t?.isEmpty() == true) {
            scheduleActually()
          }
        }
      }

      wm.getStatusesByTag(TAG).observeForever(isAlreadyScheduledObserver)
    }

    private fun scheduleActually() {
      Timber.d("Scheduling periodical $TAG")

      val request = PeriodicWorkRequestBuilder<SyncDownWorker>(7, TimeUnit.DAYS)
          .setConstraints(CONSTRAINTS)
          .build()

      WorkManager.getInstance().enqueue(request)
    }
  }

  @Inject lateinit var syncer: Syncer

  override fun doWork(): WorkerResult {
    (applicationContext as ShronqApp).appComponent.inject(this)

    try {
      syncer.syncDown().blockingAwait()
      return WorkerResult.SUCCESS
    } catch (e: IOException) {
      return WorkerResult.RETRY
    } catch (e: Exception) {
      return WorkerResult.FAILURE
    }
  }
}
