package de.hannesstruss.shronq.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import de.hannesstruss.shronq.data.Measurement

class ListAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<ListViewHolder>() {
  private val items = mutableListOf<Measurement>()
  private val itemIdLongClicksRelay = PublishRelay.create<Int>()
  val itemIdLongClicks = itemIdLongClicksRelay.hide()

  override fun getItemCount() = items.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
    val vh = ListViewHolder.new(inflater, parent)
    vh.clickHandler = itemIdLongClicksRelay::accept
    return vh
  }

  override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
    holder.bind(items[position])
  }

  fun updateItems(newItems: List<Measurement>) {
    items.clear()
    items.addAll(newItems)
    notifyDataSetChanged()
  }
}
