package de.hannesstruss.shronq.ui.debugdrawer

import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.debugdrawer.DebugDrawerIntent.TestNotification
import de.hannesstruss.shronq.ui.notifications.LunchNotification
import javax.inject.Inject

class DebugDrawerViewModel
@Inject constructor(
    private val lunchNotification: LunchNotification
) : MviViewModel<Unit, DebugDrawerIntent>() {
  override val initialState = Unit

  override val engine = createEngine {
    on<TestNotification> {
      lunchNotification.triggerNow()
    }
  }
}
