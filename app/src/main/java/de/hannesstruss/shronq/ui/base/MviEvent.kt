package de.hannesstruss.shronq.ui.base

import io.reactivex.Observable

sealed class MviEvent<ChangeT, EffectT> {
  class Change<ChangeT>(val change: ChangeT) : MviEvent<ChangeT, Nothing>()
  class Effect<EffectT>(val effect: EffectT) : MviEvent<Nothing, EffectT>()
  object Neither : MviEvent<Nothing, Nothing>()

  companion object {
    fun nothing(): Observable<MviEvent<Nothing, Nothing>> = Observable.empty()
    fun never(): Observable<MviEvent<Nothing, Nothing>> = Observable.never()
  }
}
