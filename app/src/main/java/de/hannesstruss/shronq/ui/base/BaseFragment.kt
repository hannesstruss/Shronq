package de.hannesstruss.shronq.ui.base

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.hannesstruss.shronq.ShronqApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class BaseFragment<StateT, IntentT, ViewModelT : MviViewModel<StateT, IntentT, *>> : Fragment(), MviView<IntentT> {
  abstract protected val layout: Int
  abstract protected val viewModelClass: Class<ViewModelT>

  private var stateDisposable: Disposable? = null

  final override fun onCreateView(inflater: LayoutInflater,
                                  container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
    return inflater.inflate(layout, container, false)
  }

  protected val viewModel: ViewModelT by lazy {
    val factory = (requireContext().applicationContext as ShronqApp).appComponent.viewModelFactory()
    ViewModelProviders.of(this, factory).get(viewModelClass)
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    viewModel.attachView(this)

    stateDisposable = viewModel.states
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(::render)
  }

  override fun onDetach() {
    super.onDetach()
    viewModel.detachView()
    stateDisposable?.dispose()
  }

  abstract fun render(state: StateT)
}
