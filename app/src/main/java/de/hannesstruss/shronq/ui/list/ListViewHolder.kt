package de.hannesstruss.shronq.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.Measurement
import kotlinx.android.synthetic.main.list_item.view.txt_measured_at
import kotlinx.android.synthetic.main.list_item.view.txt_weight
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ListViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  companion object {
    fun new(inflater: LayoutInflater, parent: ViewGroup): ListViewHolder {
      return ListViewHolder(inflater.inflate(R.layout.list_item, parent, false))
    }

    private val ofLocalizedDateTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
  }

  fun bind(measurement: Measurement) {
    view.txt_measured_at.text = measurement.measuredAt.format(ofLocalizedDateTime)
    view.txt_weight.text = String.format("%.1f", measurement.weight.kilograms)
  }
}
