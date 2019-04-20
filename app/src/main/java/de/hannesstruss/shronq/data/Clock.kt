package de.hannesstruss.shronq.data

import java.time.Instant
import java.time.ZonedDateTime

interface Clock {
  fun now(): Instant
  fun nowWithZone(): ZonedDateTime
}
