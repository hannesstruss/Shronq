package de.hannesstruss.shronq.ui.s3settings

data class S3SettingsState(
  val accessKey: String,
  val secretKey: String,
  val bucket: String
) {
  companion object {
    fun initial() = S3SettingsState(
        accessKey = "",
        secretKey = "",
        bucket = ""
    )
  }
}
