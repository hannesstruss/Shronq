package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.data.sync.SyncDownWorker
import de.hannesstruss.shronq.data.sync.SyncUpWorker
import de.hannesstruss.shronq.ui.di.ActivityComponent
import de.hannesstruss.shronq.ui.notifications.LunchNotificationPublisher

interface AppGraph {
  companion object {
    fun init(app: Application): AppGraph {
      return DaggerAppComponent.builder()
          .appModule(AppModule(app))
          .build()
    }

    fun get(context: Context): AppGraph {
      return (context.applicationContext as ShronqApp).appComponent
    }
  }

  fun activityComponent(): ActivityComponent.Builder

  fun inject(syncUpWorker: SyncUpWorker)
  fun inject(syncDownWorker: SyncDownWorker)
  fun inject(lunchNotificationPublisher: LunchNotificationPublisher)
}
