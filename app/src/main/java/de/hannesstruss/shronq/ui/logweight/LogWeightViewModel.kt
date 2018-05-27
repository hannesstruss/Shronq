package de.hannesstruss.shronq.ui.logweight

import de.hannesstruss.shronq.ui.base.MviViewModel
import io.reactivex.Observable
import javax.inject.Inject

class LogWeightViewModel @Inject constructor() : MviViewModel<LogWeightState, LogWeightIntent, LogWeightAction>() {
  override val actionCreator = { intent: LogWeightIntent ->
    Observable.never<LogWeightAction>()
  }

  override val stateReducer = { state: LogWeightState, action: LogWeightAction ->
    state
  }

  override val initialState = LogWeightState.initial()
}
