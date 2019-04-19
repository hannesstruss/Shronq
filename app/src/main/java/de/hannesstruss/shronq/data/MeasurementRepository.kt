package de.hannesstruss.shronq.data

import de.hannesstruss.kotlin.extensions.toFlow
import de.hannesstruss.shronq.data.db.DbMeasurement
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.sync.SyncUpWorker
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import javax.inject.Inject

class MeasurementRepository @Inject constructor(
    private val dao: DbMeasurementDao,
    private val clock: Clock
) {
  fun getMeasurements(): Flow<List<Measurement>> {
    return dao.selectAll()
        .map { all ->
          all.map { it.toMeasurement() }
        }
        .toFlow()
  }

  fun getLatestMeasurement(): Flow<Measurement> {
    return dao.selectLatest()
        .map { it.toMeasurement() }
        .toFlow()
  }

  suspend fun insertMeasurement(weightGrams: Int) {
    val measurement = Measurement(
        weight = Weight.fromGrams(weightGrams),
        measuredAt = clock.now()
    )

    insertMeasurement(measurement)
  }

  suspend fun insertMeasurement(measurement: Measurement) {
    val dbMeasurement = DbMeasurement(
        weightGrams = measurement.weight.grams,
        measuredAt = measurement.measuredAt,
        firebaseId = null,
        isSynced = false
    )

    dao.insertAll(dbMeasurement)

    SyncUpWorker.runOnce()
  }

  suspend fun getAverageWeightBetween(from: ZonedDateTime, to: ZonedDateTime): Weight? {
    return dao.getAverageWeightBetween(from, to)?.let { Weight.fromGrams(it) }
  }

  private fun DbMeasurement.toMeasurement() = Measurement(
      weight = Weight.fromGrams(weightGrams),
      measuredAt = measuredAt
  )
}
