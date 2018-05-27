package de.hannesstruss.shronq.ui.base

import io.reactivex.Observable

interface MviView<IntentT> {
  val intents: Observable<IntentT>
}
