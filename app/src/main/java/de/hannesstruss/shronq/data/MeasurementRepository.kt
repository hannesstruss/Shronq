package de.hannesstruss.shronq.data

import de.hannesstruss.kotlin.extensions.toFlow
import de.hannesstruss.shronq.data.db.DbMeasurement
import de.hannesstruss.shronq.data.db.DbMeasurementDao
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

  fun getMeasurementsNewestFirst(): Flow<List<Measurement>> {
    return dao.selectAllNewestFirst()
        .map { all -> all.map { it.toMeasurement() } }
        .toFlow()
  }

  fun getLatestMeasurement(): Flow<Measurement> {
    return dao.selectLatest()
        .map { it.toMeasurement() }
        .toFlow()
  }

  suspend fun insertMeasurement(weight: Weight) {
    val dbMeasurement = DbMeasurement(
        weightGrams = weight.grams,
        measuredAt = clock.now(),
        timezone = clock.nowWithZone().zone.toString(),
        firebaseId = null,
        isSynced = false
    )

    dao.insertAll(dbMeasurement)
  }

  suspend fun getAverageWeightBetween(from: Instant, to: Instant): Weight? {
    return dao.getAverageWeightBetween(from, to)?.let { Weight.fromGrams(it) }
  }

  suspend fun deleteById(id: Int) {
    dao.deleteById(id)
  }

  private fun DbMeasurement.toMeasurement() = Measurement(
      dbId = id,
      weight = Weight.fromGrams(weightGrams),
      measuredAt = ZonedDateTime.ofInstant(measuredAt, ZoneId.of(timezone))
  )
}
