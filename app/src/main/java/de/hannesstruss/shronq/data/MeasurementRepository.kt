package de.hannesstruss.shronq.data

import de.hannesstruss.kotlin.extensions.toFlow
import de.hannesstruss.shronq.data.db.DbMeasurement
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.sync.SyncUpWorker
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId
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
        measuredAt = clock.nowWithZone()
    )

    insertMeasurement(measurement)
  }

  suspend fun insertMeasurement(measurement: Measurement) {
    val dbMeasurement = DbMeasurement(
        weightGrams = measurement.weight.grams,
        measuredAt = measurement.measuredAt.toInstant(),
        timezone = measurement.measuredAt.zone.toString(),
        firebaseId = null,
        isSynced = false
    )

    dao.insertAll(dbMeasurement)

    SyncUpWorker.runOnce()
  }

  suspend fun getAverageWeightBetween(from: Instant, to: Instant): Weight? {
    return dao.getAverageWeightBetween(from, to)?.let { Weight.fromGrams(it) }
  }

  private fun DbMeasurement.toMeasurement() = Measurement(
      weight = Weight.fromGrams(weightGrams),
      measuredAt = ZonedDateTime.ofInstant(measuredAt, ZoneId.of(timezone))
  )
}
