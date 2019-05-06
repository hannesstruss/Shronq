package de.hannesstruss.shronq.data.s3sync

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.PutObjectRequest
import de.hannesstruss.shronq.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Provider

class S3Syncer
@Inject constructor(
    private val context: Context,
    private val creds: S3CredentialsStore,
    private val prefs: SharedPreferences,
    private val s3clientProvider: Provider<AmazonS3Client>
) {
  companion object {
    private const val KeyLastRun = "s3_backupper_last_run"
    private const val DUMP_PREFIX = "dump-"
    private const val OBSOLETE_SUFFIX = "-obsolete"
    private val SQLITE_SUFFIXES = listOf("", "-wal", "-shm")
  }

  val lastRun: String? get() = prefs.getString(KeyLastRun, null)

  suspend fun backup() {
    Timber.d("Starting backup")

    for (suffix in SQLITE_SUFFIXES) {
      val file = File(dbFile().path + suffix)
      uploadFile(file, "${DUMP_PREFIX}${creds.deviceName}-${Instant.now()}.sqlite" + suffix)
    }

    prefs.edit {
      putString(KeyLastRun, ZonedDateTime.now().toString())
    }

    Timber.d("Backup done")
  }

  private suspend fun uploadFile(file: File, uploadedName: String) {
    val putRequest = PutObjectRequest(creds.bucket, uploadedName, file)
    val s3client = s3clientProvider.get()
    withContext(Dispatchers.IO) {
      s3client.putObject(putRequest)
    }
  }

  suspend fun getDumps(): List<String> {
    val s3client = s3clientProvider.get()
    val getRequest = ListObjectsRequest(creds.bucket, DUMP_PREFIX, null, null, Integer.MAX_VALUE)

    return withContext(Dispatchers.IO) {
      val response: ObjectListing = s3client.listObjects(getRequest)
      response.objectSummaries.map { it.key }
    }
  }

  suspend fun restore(backupName: String) {
    Timber.d("Starting restore")

    val s3client = s3clientProvider.get()
    val getRequest = GetObjectRequest(creds.bucket, backupName)

    withContext(Dispatchers.IO) {
      val response = s3client.getObject(getRequest)
      restoreFromStream(backupName, response.objectContent)
    }

    Timber.d("Restore done")
  }

  private fun restoreFromStream(backupName: String, inputStream: InputStream) {
    val tempFile = File(context.cacheDir.path + "/" + backupName)
    val source = inputStream.source().buffer()
    val sink = tempFile.sink().buffer()

    source.use { input ->
      sink.use { output ->
        output.writeAll(input)
      }
    }

    val databaseDir = File(context.dataDir.path + "/databases")
    databaseDir.listFiles()
        .filter {
          it.name.startsWith(AppDatabase.NAME) && !it.name.endsWith(OBSOLETE_SUFFIX)
        }
        .forEach {
          it.renameTo(File(it.path + OBSOLETE_SUFFIX))
        }

    tempFile.renameTo(dbFile())
  }

  private fun dbFile() = File(context.dataDir.path + "/databases/" + AppDatabase.NAME)
}
