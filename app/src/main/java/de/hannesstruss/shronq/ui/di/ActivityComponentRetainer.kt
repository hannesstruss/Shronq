package de.hannesstruss.shronq.ui.di

import android.support.v4.app.Fragment
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.ui.MainActivity

class ActivityComponentRetainer : Fragment() {
  companion object {
    private val TAG = "${ActivityComponentRetainer::class.java.canonicalName}_TAG"

    fun init(activity: MainActivity) {
      getComponent(activity)
    }

    fun getComponent(activity: MainActivity): ActivityComponent {
      val fm = activity.supportFragmentManager
      val fragment = fm.findFragmentByTag(TAG) as ActivityComponentRetainer? ?: createFragment(activity)
      return fragment.component
    }

    private fun createFragment(activity: MainActivity): ActivityComponentRetainer {
      val fragment = ActivityComponentRetainer()

      val appComponent = (activity.applicationContext as ShronqApp).appComponent
      fragment.component = appComponent.activityComponent()
          .activityModule(ActivityModule(activity))
          .build()

      val fm = activity.supportFragmentManager
      fm.beginTransaction().add(fragment, TAG).commitNow()
      return fragment
    }
  }

  lateinit var component: ActivityComponent
    private set

  init {
    retainInstance = true
  }
}
