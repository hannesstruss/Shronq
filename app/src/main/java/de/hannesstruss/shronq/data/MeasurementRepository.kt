package de.hannesstruss.shronq.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class MeasurementRepository {
  companion object {
    private const val Collection = "weights"
    private const val KeyMeasuredAt = "measured_at"
    private const val KeyWeightGrams = "weight_grams"
  }

  private val db = FirebaseFirestore.getInstance()
  private val zone = ZoneId.of("Europe/Berlin")
  private val collection get() = db.collection(Collection)

  init {
    db.firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setTimestampsInSnapshotsEnabled(true)
        .build()
  }

  fun getMeasurements(): Observable<List<Measurement>> {
    val snapshots = Observable.create<List<DocumentSnapshot>> { emitter ->
      val registration =
          collection
              .orderBy(KeyMeasuredAt, Query.Direction.ASCENDING)
              .addSnapshotListener { snapshot, error ->
                snapshot?.let {
                  emitter.onNext(snapshot.documents)
                }

                error?.let {
                  emitter.onError(error)
                }
              }

      emitter.setCancellable { registration.remove() }
    }

    return snapshots.map { documents ->
      documents.map { document ->
        Measurement(
            weight = document.getDouble(KeyWeightGrams)!!,
            measuredOn = Instant.ofEpochSecond(document.getTimestamp(KeyMeasuredAt)!!.seconds).atZone(zone)
        )
      }
    }
  }

  fun getLatestMeasurement(): Maybe<Measurement> {
    return Maybe.just(Measurement(100.0, ZonedDateTime.now()))
  }

  fun insertMeasurement(weight: Double) {
    collection.add(mapOf(
        KeyMeasuredAt to Timestamp(Instant.now().epochSecond, 0),
        KeyWeightGrams to weight
    ))
  }

  fun insertMeasurement(measurement: Measurement) {
    db.collection(Collection).add(mapOf(
        KeyMeasuredAt to Timestamp(measurement.measuredOn.toInstant().epochSecond, 0),
        KeyWeightGrams to measurement.weight
    ))
  }
}
