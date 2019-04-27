package de.hannesstruss.shronq

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.jakewharton.processphoenix.ProcessPhoenix
import de.hannesstruss.shronq.data.sync.SyncDownWorker
import de.hannesstruss.shronq.di.AppGraph
import de.hannesstruss.shronq.ui.notifications.LunchNotificationScheduler
import timber.log.Timber

class ShronqApp : Application() {
  val appComponent by lazy { AppGraph.init(this) }

  private val isMainProcess get() = !ProcessPhoenix.isPhoenixProcess(this)

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    } else {
      Bugsnag.init(this)
    }

    if (isMainProcess) {
      SyncDownWorker.schedulePeriodically()
      LunchNotificationScheduler(this).schedule()
    }

//    val credentials = BasicAWSCredentials(BuildConfig.AWS_ACCESS_KEY, BuildConfig.AWS_SECRET_KEY)
//    val s3client = AmazonS3Client(credentials, Region.getRegion(Regions.EU_CENTRAL_1))
//    val file = File(dataDir.path + "/databases/shronq.sqlite")
//    val putRequest = PutObjectRequest(BuildConfig.AWS_S3_BUCKET, "dump-${Instant.now()}.sqlite", file)
//    GlobalScope.launch {
//      withContext(Dispatchers.IO) {
//        val response = s3client.putObject(putRequest)
//        Timber.d(response.toString())
//      }
//    }
  }
}
