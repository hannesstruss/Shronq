package de.hannesstruss.shronq.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.di.DebugAppGraph

class DebugDrawerFragment : Fragment() {
  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    return inflater.inflate(R.layout.debug_drawer_content, container, false)
  }

  override fun onAttach(context: Context?) {
    DebugAppGraph.get(context!!).inject(this)
    super.onAttach(context)
  }
}
