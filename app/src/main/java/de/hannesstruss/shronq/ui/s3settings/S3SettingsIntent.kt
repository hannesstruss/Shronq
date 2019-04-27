package de.hannesstruss.shronq.ui.s3settings

sealed class S3SettingsIntent {
  data class BucketChanged(val bucket: String) : S3SettingsIntent()
}
