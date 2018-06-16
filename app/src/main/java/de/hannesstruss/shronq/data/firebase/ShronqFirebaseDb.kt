package de.hannesstruss.shronq.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.hannesstruss.shronq.BuildConfig
import de.hannesstruss.shronq.data.Measurement
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import javax.inject.Inject

class ShronqFirebaseDb @Inject constructor(
    private val firestore: FirebaseFirestore
) {
  companion object {
    private const val CollectionName = BuildConfig.FIREBASE_COLLECTION_NAME
    private const val KeyMeasuredAt = "measured_at"
    private const val KeyWeightGrams = "weight_grams"
  }

  private val collection = firestore.collection(CollectionName)
  private val zone = ZoneId.systemDefault()

  fun getAllMeasurements(): Single<List<FirebaseMeasurement>> {
    val snapshots = Single.create<List<DocumentSnapshot>> { emitter ->
      collection
          .orderBy(KeyMeasuredAt, Query.Direction.ASCENDING)
          .get()
          .addOnCompleteListener { task ->
            if (!emitter.isDisposed) {
              emitter.onSuccess(task.result.documents)
            }
          }
          .addOnFailureListener { e ->
            if (!emitter.isDisposed) {
              emitter.onError(e)
            }
          }
    }

    return snapshots
        .observeOn(Schedulers.computation())
        .map { documents ->
          documents.map { it.toMeasurement() }
        }
  }

  fun addMeasurement(measurement: Measurement): Single<FirebaseMeasurement> {
    return Single.create { emitter ->
      val task = collection.add(mapOf(
          KeyMeasuredAt to Timestamp(measurement.measuredAt.toInstant().epochSecond, 0),
          KeyWeightGrams to measurement.weightGrams.toDouble()
      ))
      task.addOnSuccessListener { result ->
        result.get()
            .addOnSuccessListener { emitter.onSuccess(it.toMeasurement()) }
            .addOnFailureListener { emitter.onError(it) }
      }
      task.addOnFailureListener { emitter.onError(it) }
    }
  }

  private fun DocumentSnapshot.toMeasurement() = FirebaseMeasurement(
      weightGrams = getDouble(KeyWeightGrams)!!.toInt(),
      measuredAt = Instant.ofEpochSecond(getTimestamp(KeyMeasuredAt)!!.seconds).atZone(zone),
      id = id
  )
}