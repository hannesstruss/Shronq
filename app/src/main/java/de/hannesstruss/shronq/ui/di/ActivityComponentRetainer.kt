package de.hannesstruss.shronq.ui.di

import androidx.fragment.app.Fragment
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.ui.MainActivity

class ActivityComponentRetainer : androidx.fragment.app.Fragment() {
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

      val fm = activity.supportFragmentManager
      fm.beginTransaction().add(fragment, TAG).commitNow()
      return fragment
    }
  }

  val component: ActivityComponent by lazy {
    val activity = requireActivity() as MainActivity
    val appComponent = (activity.applicationContext as ShronqApp).appComponent
    appComponent.activityComponent()
        .activityModule(ActivityModule(activity))
        .build()
  }

  init {
    retainInstance = true
  }
}
