package de.hannesstruss.shronq.data.importer

import android.content.Context
import com.google.gson.Gson
import de.hannesstruss.shronq.data.Measurement
import de.hannesstruss.shronq.data.MeasurementRepository
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
    private val context: Context
) {
  fun import() {
    val gson = Gson()

    val items = context.assets.open("shrnq.json").use {
      val reader = InputStreamReader(it)
      gson.fromJson(reader, ShrnqApiResponse::class.java)
    }

    val zone = ZoneId.of("Europe/Berlin")
    items
        .objects
        .map { Measurement(it.value.toInt() * 1000, LocalDateTime.parse(it.measured_on).atZone(zone)) }
        .forEach { measurementRepository.insertMeasurement(it) }
  }
}
