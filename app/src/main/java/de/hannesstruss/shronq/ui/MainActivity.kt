package de.hannesstruss.shronq.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import de.hannesstruss.android.activityholder.ActivityResult
import de.hannesstruss.android.activityholder.ActivityResultsProvider
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.di.ActivityComponentRetainer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivity : AppCompatActivity(), ActivityResultsProvider {
  private val activityResults = PublishSubject.create<ActivityResult>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    ActivityComponentRetainer.init(this)
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    findNavController(R.id.nav_host_fragment).onHandleDeepLink(intent)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    activityResults.onNext(ActivityResult(requestCode, resultCode, data))
  }

  override fun activityResults(): Observable<ActivityResult> = activityResults
}
