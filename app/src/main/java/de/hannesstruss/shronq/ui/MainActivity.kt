package de.hannesstruss.shronq.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import de.hannesstruss.android.activityholder.ActivityResult
import de.hannesstruss.android.activityholder.ActivityResultsProvider
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.ViewModelFactory
import de.hannesstruss.shronq.ui.di.ActivityComponent
import de.hannesstruss.shronq.ui.di.ActivityGraph
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ActivityResultsProvider {
  @Inject lateinit var appContainer: AppContainer
  @Inject lateinit var viewModelFactory: ViewModelFactory

  private lateinit var activityComponent: ActivityGraph

  private val activityResults = PublishSubject.create<ActivityResult>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val nonConfigInstance = lastCustomNonConfigurationInstance
    if (nonConfigInstance == null) {
      activityComponent = ActivityGraph.init(this)
    } else {
      activityComponent = nonConfigInstance as ActivityComponent
    }

    activityComponent.inject(this)

    LayoutInflater.from(this).inflate(R.layout.activity_main, appContainer.get(this))
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    findNavController(R.id.nav_host_fragment).handleDeepLink(intent)
  }

  override fun getSystemService(name: String): Any {
    if (name == ViewModelFactory.SERVICE_NAME) {
      return viewModelFactory
    }
    return super.getSystemService(name)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    activityResults.onNext(ActivityResult(requestCode, resultCode, data))
  }

  override fun onRetainCustomNonConfigurationInstance(): Any {
    return activityComponent
  }

  override fun activityResults(): Observable<ActivityResult> = activityResults
}
