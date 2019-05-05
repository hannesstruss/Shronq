package de.hannesstruss.shronq.data.s3sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import com.bugsnag.android.Bugsnag
import de.hannesstruss.shronq.di.AppGraph
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BackupToS3Worker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
  companion object {
    private const val WORK_NAME = "BackupToS3"
    private val CONSTRAINTS = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePeriodically() {
      val wm = WorkManager.getInstance()

      val request = PeriodicWorkRequestBuilder<BackupToS3Worker>(7, TimeUnit.DAYS)
          .setConstraints(CONSTRAINTS)
          .build()

      wm.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun unschedule() {
      val wm = WorkManager.getInstance()
      wm.cancelUniqueWork(WORK_NAME)
    }

    suspend fun isScheduled(): Boolean {
      val wm = WorkManager.getInstance()
      val workInfos = wm.getWorkInfosForUniqueWork(WORK_NAME).await()

      return workInfos.any {
        when (it.state) {
          WorkInfo.State.ENQUEUED,
          WorkInfo.State.RUNNING -> true
          else -> false
        }
      }
    }
  }

  @Inject lateinit var s3Backupper: S3Backupper

  override suspend fun doWork(): Result {
    AppGraph.get(context).inject(this)

    return try {
      s3Backupper.backup()
      Result.success()
    } catch (e: IOException) {
      Result.retry()
    } catch (e: Exception) {
      Bugsnag.notify(e)
      Result.failure()
    }
  }
}
