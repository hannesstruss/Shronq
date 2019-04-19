package de.hannesstruss.shronq.di

import android.content.Context
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.ui.DebugDrawerFragment

interface DebugAppGraph {
  companion object {
    fun get(context: Context): DebugAppGraph {
      return (context.applicationContext as ShronqApp).appComponent as DebugAppGraph
    }
  }

  fun inject(debugDrawerFragment: DebugDrawerFragment)
}
