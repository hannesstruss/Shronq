package de.hannesstruss.shronq.ui.s3import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay2.PublishRelay
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.s3import_fragment.backup_progress_bar
import kotlinx.android.synthetic.main.s3import_fragment.backups_list

class S3ImportFragment : BaseFragment<S3ImportState, S3ImportIntent, S3ImportViewModel>(), ConfirmationDialogFragment.Parent {
  companion object {
    private const val CONFIRMATION_DIALOG_TAG = "ConfirmationDialog"
  }

  override val layout = R.layout.s3import_fragment
  override val viewModelClass = S3ImportViewModel::class.java

  private val confirmationDialogDismissals = PublishRelay.create<Unit>()
  private val confirmationDialogConfirmations = PublishRelay.create<Unit>()

  private lateinit var adapter: BackupsAdapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    adapter = BackupsAdapter(LayoutInflater.from(view.context))
    backups_list.layoutManager = LinearLayoutManager(view.context)
    backups_list.adapter = adapter
  }

  override fun intents(): Observable<S3ImportIntent> {
    return Observable.mergeArray(
        adapter.clickedBackupNames.map { S3ImportIntent.BackupSelected(it) },
        confirmationDialogDismissals.map { S3ImportIntent.ConfirmationDialogDismissed },
        confirmationDialogConfirmations.map { S3ImportIntent.RestoreConfirmed }
    )
  }

  override fun render(state: S3ImportState) {
    adapter.updateBackups(state.availableBackups)
    showConfirmation(state.confirmationDialogState)
    backup_progress_bar.isGone = !state.backupInProgress
  }

  private fun showConfirmation(state: ConfirmationDialogState) {
    val currentDialogFragment: DialogFragment? = childFragmentManager.findFragmentByTag(CONFIRMATION_DIALOG_TAG) as? DialogFragment
    val frag = currentDialogFragment ?: when (state) {
      is ConfirmationDialogState.Visible ->
        ConfirmationDialogFragment().apply {
          setBackupName(state.backupName)
        }
      is ConfirmationDialogState.Hidden -> null
    }

    if (state is ConfirmationDialogState.Visible && frag?.dialog?.isShowing != true) {
      frag?.show(childFragmentManager, CONFIRMATION_DIALOG_TAG)
    } else {
      frag?.dismiss()
    }
  }

  override fun confirmationDialogDismissed() {
    confirmationDialogDismissals.accept(Unit)
  }

  override fun confirmationDialogConfirmed() {
    confirmationDialogConfirmations.accept(Unit)
  }
}
