package de.hannesstruss.shronq.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Maybe
import io.reactivex.Observable
import org.threeten.bp.OffsetDateTime

class MeasurementRepository {
  init {
    val db = FirebaseFirestore.getInstance()
    db.firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setTimestampsInSnapshotsEnabled(true)
        .build()
    db
        .collection("weights")
        .addSnapshotListener { snapshot: QuerySnapshot?, error ->
          snapshot?.let {
            for (i in it) {
              println("On Fire!!!!: ${i.data["weight_grams"]}")
            }
          }
        }
  }

  fun getMeasurements(): Observable<Collection<Measurement>> {
    return Observable.never<Collection<Measurement>>().startWith(emptyList<Measurement>())
  }

  fun getLatestMeasurement(): Maybe<Measurement> {
    return Maybe.just(Measurement(100.0, OffsetDateTime.now()))
  }

  fun insertMeasurement(weight: Double) {

  }
}
