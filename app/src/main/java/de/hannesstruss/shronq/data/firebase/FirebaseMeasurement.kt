package de.hannesstruss.shronq.data.firebase

import org.threeten.bp.ZonedDateTime

data class FirebaseMeasurement(
    val id: String,
    val weightGrams: Int,
    val measuredAt: ZonedDateTime
)
