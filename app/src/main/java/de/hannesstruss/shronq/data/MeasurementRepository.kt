package de.hannesstruss.shronq.data

import de.hannesstruss.shronq.data.db.AppDatabase
import de.hannesstruss.shronq.data.db.DbMeasurement
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class MeasurementRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {
  fun getMeasurements(): Observable<List<Measurement>> {
    val dao = appDatabase.dbMeasurementDao()

    return dao.selectAll()
        .map { all ->
          all.map {
            Measurement(it.weightGrams, it.measuredAt, it.firebaseId)
          }
        }
        .toObservable()
  }

  fun getLatestMeasurement(): Observable<Measurement> {
    val dao = appDatabase.dbMeasurementDao()

    return dao.selectLatest()
        .map {
          Measurement(it.weightGrams, it.measuredAt, it.firebaseId)
        }
        .toObservable()
  }

  fun insertMeasurement(weightGrams: Int): Completable {
    val measurement = Measurement(
        weightGrams = weightGrams,
        measuredAt = ZonedDateTime.now()
    )

    return insertMeasurement(measurement)
  }

  fun insertMeasurement(measurement: Measurement): Completable {
    return Completable.fromAction {
      val dbMeasurement = DbMeasurement(
          weightGrams = measurement.weightGrams,
          measuredAt = measurement.measuredAt,
          firebaseId = null,
          isSynced = false
      )

      appDatabase.dbMeasurementDao().insertAll(dbMeasurement)
    }.subscribeOn(Schedulers.io())
  }
}
