package de.hannesstruss.shronq.ui.base

import io.reactivex.Observable

interface MviView<IntentT> {
  fun intents(): Observable<IntentT>
}
