package de.hannesstruss.shronq.ui.s3settings

sealed class S3SettingsIntent {
  object Save : S3SettingsIntent()
  data class AccessKeyChanged(val accessKey: String) : S3SettingsIntent()
  data class SecretKeyChanged(val secretKey: String) : S3SettingsIntent()
  data class BucketChanged(val bucket: String) : S3SettingsIntent()
}
