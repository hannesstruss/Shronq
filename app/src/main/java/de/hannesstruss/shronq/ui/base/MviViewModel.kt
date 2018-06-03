package de.hannesstruss.shronq.ui.base

import android.arch.lifecycle.ViewModel
import de.hannesstruss.shronq.extensions.ofType
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

abstract class MviViewModel<StateT, IntentT, ChangeT, EffectT> : ViewModel() {
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

  final override fun onCleared() {
    stateDisposable?.dispose()
  }

  protected val intents: Observable<IntentT> = viewsSubj.switchMap { it.intents }
  protected abstract val intentMapper: (IntentT) -> Observable<out MviEvent<out ChangeT, out EffectT>>
  protected open val extraEvents: Observable<MviEvent<out ChangeT, out EffectT>> = Observable.never()
  protected abstract val stateReducer: (StateT, ChangeT) -> StateT
  protected abstract val initialState: StateT

  private val allMviEvents by lazy {
    Observable.merge(
        intents.flatMap(intentMapper),
        extraEvents
    ).share()
  }

  private val allChanges by lazy {
    allMviEvents
        .ofType<MviEvent.Change<ChangeT>>()
        .map { it.change }
        .share()
  }

  private val allEffects by lazy {
    allMviEvents
        .ofType<MviEvent.Effect<EffectT>>()
        .map { it.effect }
        .share()
  }

  val states: Observable<StateT> by lazy {
    allChanges
        .scan(initialState, stateReducer)
        .distinctUntilChanged()
        .replay(1)
        .autoConnect(0, { stateDisposable = it })
  }

  val effects: Observable<EffectT> by lazy { allEffects }

  protected fun <T : ChangeT> Observable<T>.asEvents(): Observable<MviEvent<out ChangeT, out EffectT>> {
    return map { MviEvent.Change(it) }
  }

  protected fun <T : EffectT> T.asEvent(): Observable<MviEvent<out ChangeT, out EffectT>> {
    return Observable.just(MviEvent.Effect(this))
  }

  protected fun Any.asNoEvent(): Observable<MviEvent<out ChangeT, out EffectT>> = Observable.empty()
}

