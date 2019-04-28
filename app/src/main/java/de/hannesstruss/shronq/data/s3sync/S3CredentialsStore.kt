package de.hannesstruss.shronq.data.s3sync

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class S3CredentialsStore
@Inject constructor(
    private val prefs: SharedPreferences
){
  companion object {
    private const val KeyDeviceName = "s3_device_name"
    private const val KeyAccessKey = "s3_access_key"
    private const val KeySecretKey = "s3_secret_key"
    private const val KeyBucket = "s3_bucket"
  }

  val deviceName: String get() = prefs.getString(KeyDeviceName, "") ?: ""
  val accessKey: String get() = prefs.getString(KeyAccessKey, "") ?: ""
  val secretKey: String get() = prefs.getString(KeySecretKey, "") ?: ""
  val bucket: String get() = prefs.getString(KeyBucket, "") ?: ""

  fun setCredentials(deviceName: String, accessKey: String, secretKey: String, bucket: String) {
    prefs.edit {
      putString(KeyDeviceName, deviceName)
      putString(KeyAccessKey, accessKey)
      putString(KeySecretKey, secretKey)
      putString(KeyBucket, bucket)
    }
  }
}
