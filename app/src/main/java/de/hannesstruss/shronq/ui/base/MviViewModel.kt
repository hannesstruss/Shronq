package de.hannesstruss.shronq.ui.base

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import shronq.statemachine.EngineContext
import shronq.statemachine.StateMachine
import kotlin.coroutines.CoroutineContext

@FlowPreview
@ObsoleteCoroutinesApi
abstract class MviViewModel<StateT : Any, IntentT : Any> : ViewModel(), CoroutineScope {
  private val job = Job()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  abstract val initialState: StateT
  abstract val engine: StateMachine<StateT, IntentT>

  private val views = BehaviorRelay.create<Observable<IntentT>>()

  val intents: Observable<IntentT> = views.switchMap { it }
  val states: Observable<StateT> by lazy {
    // TODO: Start engine only on subscription.
    engine.start()
    engine.states
  }

  fun attachView(intents: Observable<IntentT>) {
    views.accept(intents)
  }

  fun dropView() {
    views.accept(Observable.never())
  }

  protected fun createEngine(block: EngineContext<StateT, IntentT>.() -> Unit): StateMachine<StateT, IntentT> {
    return StateMachine(
        coroutineScope = this,
        initialState = initialState,
        events = intents,
        initializer = block
    )
  }

  override fun onCleared() {
    super.onCleared()
    job.cancel()
  }
}
