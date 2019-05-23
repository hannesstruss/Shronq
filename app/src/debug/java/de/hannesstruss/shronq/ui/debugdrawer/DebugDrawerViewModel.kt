package de.hannesstruss.shronq.ui.debugdrawer

import android.content.Context
import com.jakewharton.processphoenix.ProcessPhoenix
import shronq.statemachine.StateMachineViewModel
import de.hannesstruss.shronq.ui.debugdrawer.DebugDrawerIntent.RestartApp
import de.hannesstruss.shronq.ui.debugdrawer.DebugDrawerIntent.TestNotification
import de.hannesstruss.shronq.ui.notifications.LunchNotification
import javax.inject.Inject

class DebugDrawerViewModel
@Inject constructor(
    private val lunchNotification: LunchNotification,
    private val context: Context
) : StateMachineViewModel<Unit, DebugDrawerIntent>() {
  override val initialState = Unit

  override val engine = createEngine {
    on<RestartApp> {
      ProcessPhoenix.triggerRebirth(context)
    }

    on<TestNotification> {
      lunchNotification.triggerNow()
    }
  }
}
