package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import de.hannesstruss.shronq.ShronqApp
import de.hannesstruss.shronq.data.Clock
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.sync.SyncDownWorker
import de.hannesstruss.shronq.data.sync.SyncUpWorker
import de.hannesstruss.shronq.data.sync.Syncer
import de.hannesstruss.shronq.ui.AppContainer
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

  fun appContainer(): AppContainer
  fun dbMeasurementDao(): DbMeasurementDao
  fun clock(): Clock
  fun context(): Context
  fun firebaseFirestore(): FirebaseFirestore
  fun syncer(): Syncer

  fun inject(syncUpWorker: SyncUpWorker)
  fun inject(syncDownWorker: SyncDownWorker)
  fun inject(lunchNotificationPublisher: LunchNotificationPublisher)
}
