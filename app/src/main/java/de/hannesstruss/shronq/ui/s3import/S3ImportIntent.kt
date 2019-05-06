package de.hannesstruss.shronq.ui.s3import

sealed class S3ImportIntent {
  object ConfirmationDialogDismissed : S3ImportIntent()
  object RestoreConfirmed : S3ImportIntent()
  data class BackupSelected(val name: String) : S3ImportIntent()
}
