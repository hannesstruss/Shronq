package de.hannesstruss.shronq.ui.base

import android.arch.lifecycle.ViewModel
import de.hannesstruss.kotlin.extensions.ofType
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

abstract class MviViewModel<StateT, IntentT, ChangeT, EffectT> : ViewModel() {
  companion object {
    private const val LOGGING = false
  }

  private val viewsSubj = BehaviorSubject.create<MviView<IntentT>>()
  private val nullView: MviView<IntentT> = object : MviView<IntentT> {
    override fun intents() = Observable.never<IntentT>()
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

  protected val intents: Observable<IntentT> = viewsSubj.switchMap { it.intents() }
  protected abstract val intentMapper: (IntentT) -> Observable<out MviEvent<out ChangeT, out EffectT>>
  protected open fun extraEvents(): Observable<MviEvent<out ChangeT, out EffectT>> = Observable.never()
  protected abstract val stateReducer: (StateT, ChangeT) -> StateT
  protected abstract val initialState: StateT

  private val allMviEvents by lazy {
    Observable.merge(
        intents.logElements("Intent") .flatMap(intentMapper),
        extraEvents()
    ).share()
  }

  private val allChanges by lazy {
    allMviEvents
        .ofType<MviEvent.Change<ChangeT>>()
        .map { it.change }
        .logElements("Change")
        .share()
  }

  private val allEffects by lazy {
    allMviEvents
        .ofType<MviEvent.Effect<EffectT>>()
        .map { it.effect }
        .logElements("Effect")
        .share()
  }

  val states: Observable<StateT> by lazy {
    allChanges
        .scan(initialState, stateReducer)
        .distinctUntilChanged()
        .logElements("State")
        .replay(1)
        .autoConnect(0, { stateDisposable = it })
  }

  val effects: Observable<EffectT> by lazy { allEffects }

  protected fun <T : ChangeT> Observable<T>.asEvents(): Observable<MviEvent<out ChangeT, out EffectT>> {
    return map { MviEvent.Change(it) }
  }

  protected fun <T : ChangeT> T.changeAsEvent(): Observable<MviEvent<out ChangeT, out EffectT>> {
    return Observable.just(this).asEvents()
  }

  protected fun <T : EffectT> T.effectAsEvent(): Observable<MviEvent<out ChangeT, out EffectT>> {
    return Observable.just(MviEvent.Effect(this))
  }

  protected fun <T : EffectT> Observable<T>.effectsAsEvents(): Observable<MviEvent<out ChangeT, out EffectT>> {
    return map { MviEvent.Effect(it) }
  }

  @Suppress("unused")
  protected fun Any.asNoEvent(): Observable<MviEvent<out ChangeT, out EffectT>> = when (this) {
    is Observable<*> -> ignoreElements().toObservable()
    is Single<*> -> ignoreElement().toObservable()
    is Completable -> toObservable()
    else -> Observable.never()
  }

  protected fun lastState(): Single<StateT> = states.firstOrError()

  private fun <T> Observable<T>.logElements(name: String): Observable<T> =
      doOnNext { if (LOGGING) Timber.d("${Thread.currentThread().name}/${this@MviViewModel.javaClass.simpleName}-$name: $it") }
}

