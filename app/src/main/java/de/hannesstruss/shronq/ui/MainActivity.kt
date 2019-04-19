package de.hannesstruss.shronq.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import de.hannesstruss.android.activityholder.ActivityResult
import de.hannesstruss.android.activityholder.ActivityResultsProvider
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.di.AppGraph
import de.hannesstruss.shronq.ui.di.ActivityComponentRetainer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ActivityResultsProvider {
  @Inject lateinit var appContainer: AppContainer

  private val activityResults = PublishSubject.create<ActivityResult>()

  override fun onCreate(savedInstanceState: Bundle?) {
    AppGraph.get(this).inject(this)

    super.onCreate(savedInstanceState)

    LayoutInflater.from(this).inflate(R.layout.activity_main, appContainer.get(this))

    ActivityComponentRetainer.init(this)
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    findNavController(R.id.nav_host_fragment).handleDeepLink(intent)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    activityResults.onNext(ActivityResult(requestCode, resultCode, data))
  }

  override fun activityResults(): Observable<ActivityResult> = activityResults
}
