package de.hannesstruss.shronq.ui.s3import

import android.content.Context
import com.jakewharton.processphoenix.ProcessPhoenix
import de.hannesstruss.shronq.data.s3sync.S3Syncer
import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.s3import.S3ImportIntent.BackupSelected
import de.hannesstruss.shronq.ui.s3import.S3ImportIntent.ConfirmationDialogDismissed
import de.hannesstruss.shronq.ui.s3import.S3ImportIntent.RestoreConfirmed
import javax.inject.Inject

class S3ImportViewModel
@Inject constructor(
    private val context: Context,
    private val s3Syncer: S3Syncer
) : MviViewModel<S3ImportState, S3ImportIntent>() {
  override val initialState = S3ImportState.initial()

  override val engine = createEngine {
    onInit {
      val dumps = s3Syncer.getDumps()
      enterState { state.copy(availableBackups = dumps) }
    }

    on<BackupSelected> {
      enterState { state.copy(confirmationDialogState = ConfirmationDialogState.Visible(it.name)) }
    }

    on<ConfirmationDialogDismissed> {
      enterState { state.copy(confirmationDialogState = ConfirmationDialogState.Hidden) }
    }

    on<RestoreConfirmed> {
      val dialogState = getLatestState().confirmationDialogState as ConfirmationDialogState.Visible
      val backupName = dialogState.backupName

      enterState {
        state.copy(
            backupInProgress = true,
            confirmationDialogState = ConfirmationDialogState.Hidden
        )
      }

      s3Syncer.restore(backupName)

      ProcessPhoenix.triggerRebirth(context)
    }
  }
}
