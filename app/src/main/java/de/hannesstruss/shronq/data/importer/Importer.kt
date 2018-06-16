package de.hannesstruss.shronq.data.importer

import android.content.Context
import com.google.gson.Gson
import de.hannesstruss.shronq.data.Measurement
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.db.AppDatabase
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.io.InputStreamReader
import javax.inject.Inject

data class ShrnqApiResponse(
    val objects: List<ShrnqApiItem>
)

data class ShrnqApiItem(
    val measured_on: String,
    val value: Double
)

class Importer @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val appDatabase: AppDatabase,
    private val context: Context
) {
  fun importJsonToFirebase() {
    val gson = Gson()

    val items = context.assets.open("shrnq.json").use {
      val reader = InputStreamReader(it)
      gson.fromJson(reader, ShrnqApiResponse::class.java)
    }

    val zone = ZoneId.of("Europe/Berlin")
    items
        .objects
        .map { Measurement((it.value * 1000).toInt(), LocalDateTime.parse(it.measured_on).atZone(zone)) }
        .forEach { measurementRepository.insertMeasurement(it) }
  }

  fun importFirebaseToLocal() {
//    measurementRepository.getMeasurements()
//        .observeOn(Schedulers.io())
//        .doOnNext { measurements ->
//          val dao = appDatabase.dbMeasurementDao()
//
//          measurements.forEach { measurement ->
//            val dbMeasurement = DbMeasurement(
//                firebaseId = measurement.firebaseId!!,
//                weightGrams = measurement.weightGrams,
//                measuredAt = measurement.measuredAt,
//                isSynced = true
//            )
//
//            dao.insertAll(dbMeasurement)
//          }
//        }
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe()
  }
}
