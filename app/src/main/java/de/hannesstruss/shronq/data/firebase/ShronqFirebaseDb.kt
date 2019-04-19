package de.hannesstruss.shronq.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.hannesstruss.shronq.BuildConfig
import de.hannesstruss.shronq.data.Measurement
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShronqFirebaseDb @Inject constructor(
    firestore: FirebaseFirestore
) {
  companion object {
    private const val CollectionName = BuildConfig.FIREBASE_COLLECTION_NAME
    private const val KeyMeasuredAt = "measured_at"
    private const val KeyWeightGrams = "weight_grams"
  }

  private val collection = firestore.collection(CollectionName)
  private val zone = ZoneId.systemDefault()
  private val ctx = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

  suspend fun getAllMeasurements(): List<FirebaseMeasurement> {
    val snapshot = withContext(ctx) {
      collection.orderBy(KeyMeasuredAt, Query.Direction.ASCENDING)
          .get()
          .await()
    }

    return snapshot.documents.map { it.toMeasurement() }
  }

  suspend fun addMeasurement(measurement: Measurement): FirebaseMeasurement {
    val snapshot = withContext(ctx) {
      val task = collection.add(mapOf(
          KeyMeasuredAt to Timestamp(measurement.measuredAt.toInstant().epochSecond, 0),
          KeyWeightGrams to measurement.weight.grams.toDouble()
      ))
      task.await().get().await()
    }

    return snapshot.toMeasurement()
  }

  private fun DocumentSnapshot.toMeasurement() = FirebaseMeasurement(
      weightGrams = getDouble(KeyWeightGrams)!!.toInt(),
      measuredAt = Instant.ofEpochSecond(getTimestamp(KeyMeasuredAt)!!.seconds).atZone(zone),
      id = id
  )
}
