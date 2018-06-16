package de.hannesstruss.shronq.data

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.hannesstruss.shronq.data.db.DbMeasurement
import de.hannesstruss.shronq.data.db.DbMeasurementDao
import de.hannesstruss.shronq.data.sync.SyncWorker
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.ZonedDateTime
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

      dao.insertAll(dbMeasurement)

      val work = OneTimeWorkRequestBuilder<SyncWorker>()
          .setConstraints(SyncWorker.CONSTRAINTS)
          .build()
      WorkManager.getInstance().enqueue(work)
    }.subscribeOn(Schedulers.io())
  }

  private fun DbMeasurement.toMeasurement() = Measurement(
      weightGrams = weightGrams,
      measuredAt = measuredAt
  )
}
