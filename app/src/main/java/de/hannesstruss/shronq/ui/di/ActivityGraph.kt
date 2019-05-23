package de.hannesstruss.shronq.ui.di

import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.ui.MainActivity
import shronq.statemachine.ViewModelFactory

interface ActivityGraph {
  companion object {
    fun init(activity: MainActivity): ActivityGraph {
      val appComponent = (activity.applicationContext as ShronqApp).appComponent

      return DaggerActivityComponent.builder()
          .appGraph(appComponent)
          .activityModule(ActivityModule(activity))
          .build()
    }
  }

  fun viewModelFactory(): ViewModelFactory
  fun inject(mainActivity: MainActivity)
}
