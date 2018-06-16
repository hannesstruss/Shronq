package de.hannesstruss.shronq.data.sync

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import de.hannesstruss.shronq.ShronqApp
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SyncUpWorker : Worker() {
  companion object {
    private val TAG = SyncUpWorker::class.java.simpleName

    private val CONSTRAINTS: Constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private val EXISTING_WORK_POLICY = ExistingWorkPolicy.APPEND

    fun runOnce() {
      val request = OneTimeWorkRequestBuilder<SyncUpWorker>()
          .setConstraints(SyncUpWorker.CONSTRAINTS)
          .build()
      WorkManager.getInstance()
          .beginUniqueWork(TAG, EXISTING_WORK_POLICY, request).enqueue()
    }
  }

  @Inject lateinit var syncer: Syncer

  override fun doWork(): WorkerResult {
    (applicationContext as ShronqApp).appComponent.inject(this)

    Timber.d("Starting to sync up")

    try {
      syncer.syncUp().blockingAwait()
      return WorkerResult.SUCCESS
    } catch (e: Exception) {
      if (e is IOException) {
        Timber.d("Should retry")
        return WorkerResult.RETRY
      } else {
        Timber.e(e, "Sync failure")
        return WorkerResult.FAILURE
      }
    }
  }
}
