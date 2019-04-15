package de.hannesstruss.shronq.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import de.hannesstruss.shronq.ShronqApp
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SyncUpWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
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

  override suspend fun doWork(): Result {
    (applicationContext as ShronqApp).appComponent.inject(this)

    Timber.d("Starting to sync up")

    return try {
      syncer.syncUp()
      Result.success()
    } catch (e: Exception) {
      if (e is IOException) {
        Timber.d("Should retry")
        Result.retry()
      } else {
        Timber.e(e, "Sync failure")
        Result.failure()
      }
    }
  }
}
