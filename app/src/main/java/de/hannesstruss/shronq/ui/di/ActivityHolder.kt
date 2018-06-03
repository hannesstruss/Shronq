package de.hannesstruss.shronq.ui.di

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

class ActivityHolder : Fragment() {
  companion object {
    private val TAG = "${ActivityHolder::class.java.canonicalName}_TAG"

    fun get(activity: AppCompatActivity): ActivityHolder {
      val fm = activity.supportFragmentManager
      return fm.findFragmentByTag(TAG) as ActivityHolder? ?: create(fm)
    }

    private fun create(fm: FragmentManager): ActivityHolder {
      val fragment = ActivityHolder()
      fm.beginTransaction().add(fragment, TAG).commitNow()
      return fragment
    }
  }

  init {
    retainInstance = true
  }

  val activity: Activity get() = requireActivity()
}
