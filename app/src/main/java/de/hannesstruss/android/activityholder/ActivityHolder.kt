package de.hannesstruss.android.activityholder

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

class ActivityHolder : Fragment() {
  companion object {
    private val TAG = "${ActivityHolder::class.java.canonicalName}_TAG"

    fun <ActivityT> get(activity: ActivityT): ActivityHolder where ActivityT : AppCompatActivity, ActivityT : ActivityResultsProvider {
      val fm = activity.supportFragmentManager
      return fm.findFragmentByTag(TAG) as ActivityHolder? ?: create(fm)
    }

    private fun create(fm: FragmentManager): ActivityHolder {
      val fragment = ActivityHolder()
      fm.beginTransaction().add(fragment, TAG).commitNow()
      return fragment
    }
  }

  private val activityResultsSubj = BehaviorSubject.create<Observable<ActivityResult>>()

  val activityResults: Observable<ActivityResult> = activityResultsSubj
      .switchMap { it }
      .publish()
      .autoConnect(0)

  init {
    retainInstance = true
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    Timber.d("Pushing new ActivityResultsProvider")
    activityResultsSubj.onNext((context as ActivityResultsProvider).activityResults())
  }

  override fun onDetach() {
    super.onDetach()
    activityResultsSubj.onNext(Observable.never())
  }

  fun activity(): Activity = requireActivity()
}
