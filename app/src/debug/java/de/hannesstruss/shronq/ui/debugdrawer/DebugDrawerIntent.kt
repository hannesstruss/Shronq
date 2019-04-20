package de.hannesstruss.shronq.ui.debugdrawer

sealed class DebugDrawerIntent {
  object TestNotification : DebugDrawerIntent()
  object RestartApp : DebugDrawerIntent()
}
