package de.hannesstruss.shronq.ui.s3settings

data class S3SettingsState(
    val deviceName: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val syncEnabled: Boolean,
    val manualSyncRunning: Boolean,
    val lastSyncRun: String?
) {
  companion object {
    fun initial() = S3SettingsState(
        deviceName = "",
        accessKey = "",
        secretKey = "",
        bucket = "",
        syncEnabled = false,
        manualSyncRunning = false,
        lastSyncRun = null
    )
  }

  val runSyncButtonEnabled get() = !manualSyncRunning

  val lastSyncRunText: String = "Last run: ${lastSyncRun ?: "never"}"
}
