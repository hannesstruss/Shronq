package de.hannesstruss.shronq.ui.di

import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.ui.MainActivity
import de.hannesstruss.shronq.ui.base.ViewModelFactory

interface ActivityGraph {
  companion object {
    fun init(activity: MainActivity): ActivityGraph {
      val appComponent = (activity.applicationContext as ShronqApp).appComponent
      return appComponent.activityComponent()
          .activityModule(ActivityModule(activity))
          .build()
    }
  }

  fun viewModelFactory(): ViewModelFactory
  fun inject(mainActivity: MainActivity)
}
