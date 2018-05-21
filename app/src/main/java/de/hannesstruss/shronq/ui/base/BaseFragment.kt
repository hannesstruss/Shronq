package de.hannesstruss.shronq.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.di.AppComponent

abstract class BaseFragment : Fragment() {
  abstract protected val layout: Int

  override fun onAttach(context: Context) {
    super.onAttach(context)

    onInject((context.applicationContext as ShronqApp).appComponent)
  }

  final override fun onCreateView(inflater: LayoutInflater,
                                  container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
    return inflater.inflate(layout, container, false)
  }

  abstract fun onInject(appComponent: AppComponent)
}
