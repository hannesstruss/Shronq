package de.hannesstruss.shronq.data.s3sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.DirectExecutor
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bugsnag.android.Bugsnag
import com.google.common.util.concurrent.ListenableFuture
import de.hannesstruss.shronq.di.AppGraph
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    // Copied from androidx.work
    private suspend inline fun <R> ListenableFuture<R>.await(): R {
      // Fast path
      if (isDone) {
        try {
          return get()
        } catch (e: ExecutionException) {
          throw e.cause ?: e
        }
      }
      return suspendCancellableCoroutine { cancellableContinuation ->
        addListener(Runnable {
          try {
            cancellableContinuation.resume(get())
          } catch (throwable: Throwable) {
            val cause = throwable.cause ?: throwable
            when (throwable) {
              is CancellationException -> cancellableContinuation.cancel(cause)
              else -> cancellableContinuation.resumeWithException(cause)
            }
          }
        }, DirectExecutor.INSTANCE)
      }
    }
  }

  @Inject lateinit var s3Syncer: S3Syncer

  override suspend fun doWork(): Result {
    AppGraph.get(context).inject(this)

    return try {
      s3Syncer.backup()
      Result.success()
    } catch (e: IOException) {
      Result.retry()
    } catch (e: Exception) {
      Bugsnag.notify(e)
      Result.failure()
    }
  }
}
