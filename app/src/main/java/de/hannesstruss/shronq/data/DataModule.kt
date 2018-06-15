package de.hannesstruss.shronq.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import de.hannesstruss.shronq.data.db.DbModule

@Module(
    includes = [
      DbModule::class
    ]
)
class DataModule {
  @Provides fun firebaseFirestore() = FirebaseFirestore.getInstance().apply {
    firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setTimestampsInSnapshotsEnabled(true)
        .build()
  }
}
