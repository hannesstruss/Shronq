package de.hannesstruss.shronq.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.shronq.data.Measurement

class ListAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<ListViewHolder>() {
  private val items = mutableListOf<Measurement>()

  override fun getItemCount() = items.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
    return ListViewHolder.new(inflater, parent)
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
