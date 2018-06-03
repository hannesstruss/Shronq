package de.hannesstruss.shronq.di

import android.app.Application
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application) {
  @Provides fun firebaseFirestore() = FirebaseFirestore.getInstance().apply {
    firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setTimestampsInSnapshotsEnabled(true)
        .build()
  }

  @Provides fun context(): Context = app
}