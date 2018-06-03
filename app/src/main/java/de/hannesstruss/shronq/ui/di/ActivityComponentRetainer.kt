package de.hannesstruss.shronq.ui.di

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import de.hannesstruss.shronq.ShronqApp

class ActivityComponentRetainer : Fragment() {
  companion object {
    private val TAG = "${ActivityComponentRetainer::class.java.canonicalName}_TAG"

    fun init(activity: AppCompatActivity) {
      getComponent(activity)
    }

    fun getComponent(activity: AppCompatActivity): ActivityComponent {
      val fm = activity.supportFragmentManager
      val fragment = fm.findFragmentByTag(TAG) as ActivityComponentRetainer? ?: createFragment(activity)
      return fragment.component
    }

    private fun createFragment(activity: AppCompatActivity): ActivityComponentRetainer {
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
