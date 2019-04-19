package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import dagger.Component
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.data.sync.SyncDownWorker
import de.hannesstruss.shronq.data.sync.SyncUpWorker
import de.hannesstruss.shronq.ui.di.ActivityComponent
import de.hannesstruss.shronq.ui.notifications.LunchNotificationPublisher
import javax.inject.Singleton

@Component(
    modules = [AppModule::class]
)
@Singleton
interface AppComponent {
  companion object {
    fun init(app: Application): AppComponent {
      return DaggerAppComponent.builder()
          .appModule(AppModule(app))
          .build()
    }

    fun get(context: Context): AppComponent {
      return (context.applicationContext as ShronqApp).appComponent
    }
  }

  fun activityComponent(): ActivityComponent.Builder

  fun inject(syncUpWorker: SyncUpWorker)
  fun inject(syncDownWorker: SyncDownWorker)
  fun inject(lunchNotificationPublisher: LunchNotificationPublisher)
}
