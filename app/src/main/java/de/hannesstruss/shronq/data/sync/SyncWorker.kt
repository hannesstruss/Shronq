package de.hannesstruss.shronq.data.sync

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.Worker
import de.hannesstruss.shronq.ShronqApp
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SyncWorker : Worker() {
  companion object {
    val CONSTRAINTS: Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
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
