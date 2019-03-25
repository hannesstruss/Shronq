package de.hannesstruss.shronq.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import de.hannesstruss.kotlin.delegates.idempotent
import de.hannesstruss.shronq.data.Measurement
import java.time.Period
import java.time.ZonedDateTime

class HomeChart(context: Context, attrs: AttributeSet?) : View(context, attrs) {
  private val linePaint = Paint().apply {
    color = 0xFFFFFFFF.toInt()
    isAntiAlias = true
    strokeWidth = 4f
  }

  var measurements: List<Measurement> by idempotent(emptyList()) {
    invalidate()
  }

  private var earliestDate: ZonedDateTime? = null

  init {
    setBackgroundColor(0xFF0000FF.toInt())
    setWillNotDraw(false)
  }

  fun setPeriod(period: Period?) {
    earliestDate = period?.let {
      ZonedDateTime.now().minus(it)
    }
    invalidate()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    // TODO: don't allocate all this garbage

    val values = measurements
        .filter { earliestDate?.isBefore(it.measuredAt) ?: true }

    if (values.size < 2) return

    val min = values.minBy { it.weightGrams }!!.weightGrams.toDouble()
    val max = values.maxBy { it.weightGrams }!!.weightGrams.toDouble()
    val minTimestamp = values.minBy { it.measuredAt.toEpochSecond() }!!.measuredAt.toEpochSecond()
    val maxTimestamp = values.maxBy { it.measuredAt.toEpochSecond() }!!.measuredAt.toEpochSecond()

    values
        .zipWithNext()
        .forEach { (from, to) ->
          val fromTimestamp = from.measuredAt.toEpochSecond()
          val toTimestamp = to.measuredAt.toEpochSecond()
          val fromTimestampRatio = (fromTimestamp - minTimestamp).toDouble() / (maxTimestamp - minTimestamp)
          val toTimestampRatio = (toTimestamp - minTimestamp).toDouble() / (maxTimestamp - minTimestamp)

          val fromValue = from.weightGrams.toDouble()
          val toValue = to.weightGrams.toDouble()
          val fromValueRatio = (fromValue - min) / (max - min)
          val toValueRatio = (toValue - min) / (max - min)

          canvas.drawLine(
              (fromTimestampRatio * width).toFloat(),
              ((1 - fromValueRatio) * height).toFloat(),
              (toTimestampRatio * width).toFloat(),
              ((1 - toValueRatio) * height).toFloat(),
              linePaint
          )
        }
  }
}
