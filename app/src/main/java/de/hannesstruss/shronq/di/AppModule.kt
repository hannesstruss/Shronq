package de.hannesstruss.shronq.di

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import de.hannesstruss.shronq.ui.ViewModelModule

@Module(
    includes = [
      ViewModelModule::class
    ]
)
class AppModule(private val app: Application) {
  @Provides fun firebaseFirestore() = FirebaseFirestore.getInstance().apply {
    firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setTimestampsInSnapshotsEnabled(true)
        .build()
  }
}
