package de.hannesstruss.shronq.ui.base

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

abstract class MviViewModel<StateT, IntentT, ActionT> : ViewModel() {
  private val viewsSubj = BehaviorSubject.create<MviView<IntentT>>()
  private val nullView: MviView<IntentT> = object : MviView<IntentT> {
    override val intents = Observable.never<IntentT>()
  }

  private var stateDisposable: Disposable? = null

  fun attachView(view: MviView<IntentT>) {
    viewsSubj.onNext(view)
  }

  fun detachView() {
    viewsSubj.onNext(nullView)
  }

  fun dispose() {
    stateDisposable?.dispose()
  }

  final override fun onCleared() {
    dispose()
  }

  protected val intents: Observable<IntentT> = viewsSubj.switchMap { it.intents }
  protected abstract val actionCreator: (IntentT) -> Observable<ActionT>
  protected open val extraActions: Observable<ActionT> = Observable.never()
  protected abstract val stateReducer: (StateT, ActionT) -> StateT
  protected abstract val initialState: StateT

  val states: Observable<StateT> by lazy {
    intents
        .flatMap(actionCreator)
        .mergeWith(extraActions)
        .scan(initialState, stateReducer)
        .distinctUntilChanged()
        .replay(1)
        .autoConnect(0, { stateDisposable = it })
  }
}
