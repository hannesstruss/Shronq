package de.hannesstruss.shronq.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import de.hannesstruss.shronq.data.Measurement
import kotlin.properties.Delegates

class HomeChart(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
  private val linePaint = Paint().apply {
    color = 0xFFFFFFFF.toInt()
    isAntiAlias = true
    strokeWidth = 4f
  }

  var measurements: List<Measurement> by Delegates.observable(emptyList()) { _, _, newValue ->
    invalidate()
  }

  init {
    setBackgroundColor(0xFF0000FF.toInt())
    setWillNotDraw(false)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    if (measurements.size < 2) return

    val min = measurements.minBy { it.weight }!!.weight
    val max = measurements.maxBy { it.weight }!!.weight
    val minTimestamp = measurements.minBy { it.measuredOn.toEpochSecond() }!!.measuredOn.toEpochSecond()
    val maxTimestamp = measurements.maxBy { it.measuredOn.toEpochSecond() }!!.measuredOn.toEpochSecond()

    measurements.zipWithNext().forEach { (from, to) ->
      val fromTimestamp = from.measuredOn.toEpochSecond()
      val toTimestamp = to.measuredOn.toEpochSecond()
      val fromTimestampRatio = (fromTimestamp - minTimestamp).toDouble() / (maxTimestamp - minTimestamp)
      val toTimestampRatio = (toTimestamp - minTimestamp).toDouble() / (maxTimestamp - minTimestamp)

      val fromValue = from.weight
      val toValue = to.weight
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
