package de.hannesstruss.shronq.data

import org.threeten.bp.ZonedDateTime

data class Measurement(
    val weightGrams: Int,
    val measuredAt: ZonedDateTime
)
