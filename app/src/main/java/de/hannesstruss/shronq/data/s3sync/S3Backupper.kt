package de.hannesstruss.shronq.data.s3sync

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject

class S3Backupper
@Inject constructor(
    private val context: Context,
    private val creds: S3CredentialsStore,
    private val prefs: SharedPreferences
) {
  companion object {
    private const val KeyLastRun = "s3_backupper_last_run"
  }

  val lastRun: String? get() = prefs.getString(KeyLastRun, null)

  suspend fun backup() {
    Timber.d("Starting backup")
    val credentials = BasicAWSCredentials(creds.accessKey, creds.secretKey)
    val s3client = AmazonS3Client(credentials, Region.getRegion(Regions.EU_CENTRAL_1))
    val file = File(context.dataDir.path + "/databases/shronq.sqlite")
    val putRequest = PutObjectRequest(creds.bucket, "dump-${creds.deviceName}-${Instant.now()}.sqlite", file)
    withContext(Dispatchers.IO) {
      val response = s3client.putObject(putRequest)
      Timber.d(response.toString())
    }

    prefs.edit {
      putString(KeyLastRun, ZonedDateTime.now().toString())
    }

    Timber.d("Backup done")
  }
}
