package de.hannesstruss.shronq.data.sync

import de.hannesstruss.shronq.data.Measurement
import de.hannesstruss.shronq.data.Weight
import de.hannesstruss.shronq.data.db.DbMeasurement
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.firebase.ShronqFirebaseDb
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

/** Syncs local with Firebase db */
class Syncer @Inject constructor(
    private val firebaseDb: ShronqFirebaseDb,
    private val measurementDao: DbMeasurementDao
) {
  suspend fun syncDown() {
    val measurements = firebaseDb.getAllMeasurements()

    for (measurement in measurements) {
      val localId = measurementDao.getIdForFirebaseId(measurement.id)

      val dbMeasurement = DbMeasurement(
          id = localId ?: DbMeasurement.NO_ID,
          weightGrams = measurement.weightGrams,
          measuredAt = measurement.measuredAt.toInstant(),
          timezone = measurement.measuredAt.zone.toString(),
          firebaseId = measurement.id,
          isSynced = true
      )

      if (localId == null) {
        Timber.d("Updating")
        measurementDao.insertAll(dbMeasurement)
      } else {
        Timber.d("Inserting")
        measurementDao.update(dbMeasurement)
      }
    }
  }

  suspend fun syncUp() {
    val unsyncedMeasurements = measurementDao.getUnsyncedMeasurements()

    for (dbMeasurement in unsyncedMeasurements) {
      val measurement = Measurement(
          weight = Weight.fromGrams(dbMeasurement.weightGrams),
          measuredAt = ZonedDateTime.ofInstant(dbMeasurement.measuredAt, ZoneId.of(dbMeasurement.timezone))
      )

      val firebaseMeasurement = firebaseDb.addMeasurement(measurement)
      val selected = measurementDao.selectById(dbMeasurement.id)
      if (selected != null) {
        val updated = selected.copy(firebaseId = firebaseMeasurement.id, isSynced = true)
        measurementDao.update(updated)
      }
    }
  }
}
