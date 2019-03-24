package de.hannesstruss.shronq.data

import java.time.ZonedDateTime

data class Measurement(
    val weightGrams: Int,
    val measuredAt: ZonedDateTime
)
