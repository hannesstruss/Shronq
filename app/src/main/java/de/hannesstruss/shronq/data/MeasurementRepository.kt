package de.hannesstruss.shronq.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.Observable
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import javax.inject.Inject

class MeasurementRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
  companion object {
    private const val Collection = "weights"
    private const val KeyMeasuredAt = "measured_at"
    private const val KeyWeightGrams = "weight_grams"
  }

  private val zone = ZoneId.of("Europe/Berlin")
  private val collection get() = db.collection(Collection)

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
      documents.map { it.toMeasurement() }
    }
  }

  fun getLatestMeasurement(): Observable<Measurement> {
    val snapshots = Observable.create<DocumentSnapshot> { emitter ->
      val registration =
          collection
              .orderBy(KeyMeasuredAt, Query.Direction.DESCENDING)
              .limit(1)
              .addSnapshotListener { snapshot, error ->
                snapshot?.let {
                  if (!snapshot.isEmpty) {
                    emitter.onNext(snapshot.documents[0])
                  }
                }

                error?.let { emitter.onError(it) }
              }

      emitter.setCancellable { registration.remove() }
    }

    return snapshots.map { it.toMeasurement() }
  }

  fun insertMeasurement(weight: Double) {
    collection.add(mapOf(
        KeyMeasuredAt to Timestamp(Instant.now().epochSecond, 0),
        KeyWeightGrams to weight
    ))
  }

  fun insertMeasurement(measurement: Measurement) {
    db.collection(Collection).add(mapOf(
        KeyMeasuredAt to Timestamp(measurement.measuredAt.toInstant().epochSecond, 0),
        KeyWeightGrams to measurement.weightGrams.toDouble()
    ))
  }

  private fun DocumentSnapshot.toMeasurement() = Measurement(
      weightGrams = getDouble(KeyWeightGrams)!!.toInt(),
      measuredAt = Instant.ofEpochSecond(getTimestamp(KeyMeasuredAt)!!.seconds).atZone(zone)
  )
}
