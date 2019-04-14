package de.hannesstruss.shronq.ui.base

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import shronq.mvi.EngineContext
import shronq.mvi.MviEngine
import kotlin.coroutines.CoroutineContext

abstract class MviViewModel2<StateT : Any, IntentT : Any> : ViewModel(), CoroutineScope {
  private val job = Job()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  abstract val initialState: StateT
  abstract val engine: MviEngine<StateT, IntentT>

  private val views = BehaviorRelay.create<Observable<IntentT>>()

  val intents: Observable<IntentT> = views.switchMap { it }
  private var statesDisposable: Disposable? = null
  val states: Observable<StateT> by lazy {
    engine.start()
    engine.states.replay(1).autoConnect(1) { statesDisposable = it }
  }

  fun attachView(intents: Observable<IntentT>) {
    views.accept(intents)
  }

  fun dropView() {
    views.accept(Observable.never())
  }

  protected fun createEngine(block: EngineContext<StateT, IntentT>.() -> Unit): MviEngine<StateT, IntentT> {
    return MviEngine(
        coroutineScope = this,
        initialState = initialState,
        intents = intents,
        initializer = block
    )
  }

  override fun onCleared() {
    super.onCleared()

    statesDisposable?.dispose()
    job.cancel()
  }
}
