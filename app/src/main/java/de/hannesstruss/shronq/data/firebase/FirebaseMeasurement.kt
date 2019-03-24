package de.hannesstruss.shronq.data.firebase

import java.time.ZonedDateTime

data class FirebaseMeasurement(
    val id: String,
    val weightGrams: Int,
    val measuredAt: ZonedDateTime
)
