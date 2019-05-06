package de.hannesstruss.shronq.ui.s3import

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmationDialogFragment : DialogFragment() {
  interface Parent {
    fun confirmationDialogDismissed()
    fun confirmationDialogConfirmed()
  }

  private var backupName = ""
  fun setBackupName(name: String) {
    backupName = name
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity!!)
        .setTitle("Really restore?")
        .setMessage("Really restore from $backupName?")
        .setPositiveButton("Restore") { _, _ ->
          (parentFragment as Parent).confirmationDialogConfirmed()
        }
        .setNegativeButton("Cancel") { _, _ ->
          (parentFragment as Parent).confirmationDialogDismissed()
        }
        .setOnDismissListener {
          (parentFragment as Parent).confirmationDialogDismissed()
        }
        .create()
  }
}
