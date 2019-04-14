package de.hannesstruss.shronq.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.hannesstruss.shronq.ui.MainActivity
import de.hannesstruss.shronq.ui.di.ActivityComponentRetainer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseFragment2<StateT : Any, IntentT : Any, ViewModelT : MviViewModel2<StateT, IntentT>> : Fragment() {
  abstract val viewModelClass: Class<ViewModelT>
  abstract fun intents(): Observable<IntentT>
  abstract fun render(state: StateT)

  private lateinit var viewModel: ViewModelT
  private var stateDisposable: Disposable? = null

  private fun initViewModel() {
    val factory = ActivityComponentRetainer.getComponent(requireActivity() as MainActivity).viewModelFactory()
    viewModel = ViewModelProviders.of(this, factory).get(viewModelClass)
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)

    initViewModel()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    stateDisposable = viewModel.states.subscribe { render(it) }
    viewModel.attachView(intents())
  }

  override fun onDestroyView() {
    super.onDestroyView()

    stateDisposable?.dispose()
    viewModel.dropView()
  }
}
