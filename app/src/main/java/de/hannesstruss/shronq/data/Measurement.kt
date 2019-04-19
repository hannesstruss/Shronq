package de.hannesstruss.shronq.data

import java.time.ZonedDateTime

data class Measurement(
    val weight: Weight,
    val measuredAt: ZonedDateTime
)
