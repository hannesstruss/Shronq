package de.hannesstruss.shronq.data.sync

import de.hannesstruss.shronq.data.Measurement
import de.hannesstruss.shronq.data.db.DbMeasurement
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.firebase.ShronqFirebaseDb
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import javax.inject.Inject

/** Syncs local with Firebase db */
class Syncer @Inject constructor(
    private val firebaseDb: ShronqFirebaseDb,
    private val measurementDao: DbMeasurementDao
) {
  suspend fun syncDown() {
    val measurements = firebaseDb.getAllMeasurements().await()

    for (measurement in measurements) {
      val localId = measurementDao.getIdForFirebaseId(measurement.id)

      val dbMeasurement = DbMeasurement(
          id = localId ?: DbMeasurement.NO_ID,
          weightGrams = measurement.weightGrams,
          measuredAt = measurement.measuredAt,
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

  fun syncUp(): Completable {
    return measurementDao.getUnsyncedMeasurements()
        .subscribeOn(Schedulers.io())
        .flatMapObservable { Observable.fromIterable(it) }
        .flatMapCompletable { dbMeasurement ->

          val measurement = Measurement(
              weightGrams = dbMeasurement.weightGrams,
              measuredAt = dbMeasurement.measuredAt
          )

          firebaseDb.addMeasurement(measurement)
              .observeOn(Schedulers.io())
              .flatMapMaybe { firebaseMeasurement ->
                measurementDao.selectById(dbMeasurement.id)
                    .doOnSuccess {
                      val updated = it.copy(firebaseId = firebaseMeasurement.id, isSynced = true)
                      measurementDao.update(updated)
                    }
              }
              .ignoreElement()
        }
  }
}
