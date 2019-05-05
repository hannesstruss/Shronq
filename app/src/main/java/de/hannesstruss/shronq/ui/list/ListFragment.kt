package de.hannesstruss.shronq.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.list_fragment.list

class ListFragment : BaseFragment<ListState, ListIntent, ListViewModel>() {
  override val layout = R.layout.list_fragment
  override val viewModelClass = ListViewModel::class.java

  private lateinit var adapter: ListAdapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val context = view.context
    list.layoutManager = LinearLayoutManager(context)
    adapter = ListAdapter(LayoutInflater.from(context))
    list.adapter = adapter
  }

  override fun intents(): Observable<ListIntent> {
    return adapter.itemIdLongClicks.map { ListIntent.DeleteItem(it) }
  }

  override fun render(state: ListState) {
    adapter.updateItems(state.measurements)
  }
}
