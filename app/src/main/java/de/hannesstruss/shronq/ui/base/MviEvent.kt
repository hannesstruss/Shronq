package de.hannesstruss.shronq.ui.base

import io.reactivex.Observable

sealed class MviEvent<ChangeT, EffectT> {
  data class Change<ChangeT>(val change: ChangeT) : MviEvent<ChangeT, Nothing>()
  data class Effect<EffectT>(val effect: EffectT) : MviEvent<Nothing, EffectT>()

  companion object {
    fun nothing(): Observable<MviEvent<Nothing, Nothing>> = Observable.empty()
    fun never(): Observable<MviEvent<Nothing, Nothing>> = Observable.never()
  }
}
