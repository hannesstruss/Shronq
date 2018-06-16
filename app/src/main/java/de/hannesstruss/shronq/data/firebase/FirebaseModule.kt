package de.hannesstruss.shronq.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {
  @Provides fun firebaseFirestore() = FirebaseFirestore.getInstance().apply {
    firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setTimestampsInSnapshotsEnabled(true)
        .build()
  }
}
