package de.hannesstruss.shronq.data

import de.hannesstruss.shronq.data.db.DbMeasurement
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.sync.SyncUpWorker
import io.reactivex.Observable
import java.time.ZonedDateTime
import javax.inject.Inject

class MeasurementRepository @Inject constructor(
    private val dao: DbMeasurementDao
) {
  fun getMeasurements(): Observable<List<Measurement>> {
    return dao.selectAll()
        .map { all ->
          all.map { it.toMeasurement() }
        }
        .toObservable()
  }

  fun getLatestMeasurement(): Observable<Measurement> {
    return dao.selectLatest()
        .map { it.toMeasurement() }
        .toObservable()
  }

  suspend fun insertMeasurement(weightGrams: Int) {
    val measurement = Measurement(
        weightGrams = weightGrams,
        measuredAt = ZonedDateTime.now()
    )

    insertMeasurement(measurement)
  }

  suspend fun insertMeasurement(measurement: Measurement) {
    val dbMeasurement = DbMeasurement(
        weightGrams = measurement.weightGrams,
        measuredAt = measurement.measuredAt,
        firebaseId = null,
        isSynced = false
    )

    dao.insertAll(dbMeasurement)

    SyncUpWorker.runOnce()
  }

  private fun DbMeasurement.toMeasurement() = Measurement(
      weightGrams = weightGrams,
      measuredAt = measuredAt
  )
}
