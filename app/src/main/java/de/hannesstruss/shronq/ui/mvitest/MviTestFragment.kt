package de.hannesstruss.shronq.ui.mvitest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.hannesstruss.shronq.R

class MviTestFragment : Fragment() {
  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.mvi_test_fragment, container, false)
  }
}
