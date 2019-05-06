package de.hannesstruss.shronq.ui.s3import

data class S3ImportState(
    val availableBackups: List<String>,
    val confirmationDialogState: ConfirmationDialogState,
    val backupInProgress: Boolean
) {
  companion object {
    fun initial() = S3ImportState(
        availableBackups = emptyList(),
        confirmationDialogState = ConfirmationDialogState.Hidden,
        backupInProgress = false
    )
  }
}

sealed class ConfirmationDialogState {
  object Hidden : ConfirmationDialogState()
  data class Visible(val backupName: String) : ConfirmationDialogState()
}
