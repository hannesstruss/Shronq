package de.hannesstruss.shronq.data

import java.time.ZonedDateTime

interface Clock {
  fun now(): ZonedDateTime
}
