package de.hannesstruss.shronq.ui.s3import

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.shronq.R
import kotlinx.android.synthetic.main.s3import_backup_item.view.txt_backup_name

class BackupViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  companion object {
    fun new(inflater: LayoutInflater, parent: ViewGroup): BackupViewHolder {
      return BackupViewHolder(inflater.inflate(R.layout.s3import_backup_item, parent, false))
    }
  }

  var clickHandler: ((backupName: String) -> Unit)? = null
  var backupName: String = ""
    private set

  init {
    view.setOnClickListener {
      clickHandler?.invoke(backupName)
    }
  }

  fun bind(backupName: String) {
    this.backupName = backupName
    view.txt_backup_name.text = backupName
  }
}
