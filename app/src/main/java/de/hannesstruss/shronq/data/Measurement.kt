package de.hannesstruss.shronq.data

import java.time.ZonedDateTime

data class Measurement(
    val dbId: Int,
    val weight: Weight,
    val measuredAt: ZonedDateTime
)
