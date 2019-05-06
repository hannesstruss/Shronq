package de.hannesstruss.shronq.ui.s3import

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay

class BackupsAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<BackupViewHolder>() {
  private val items = mutableListOf<String>()
  private val clickedBackupNamesRelay = PublishRelay.create<String>()
  val clickedBackupNames = clickedBackupNamesRelay.hide()

  override fun getItemCount() = items.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
    val vh = BackupViewHolder.new(inflater, parent)

    vh.clickHandler = clickedBackupNamesRelay::accept

    return vh
  }

  override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
    holder.bind(items[position])
  }

  fun updateBackups(backups: List<String>) {
    items.clear()
    items.addAll(backups)
    notifyDataSetChanged()
  }
}
