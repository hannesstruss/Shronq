package de.hannesstruss.shronq.data.db

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
  companion object {
    @JvmStatic @TypeConverter fun fromZonedDateTime(instant: Instant): String = instant.toString()
    @JvmStatic @TypeConverter fun toZonedDateTime(str: String): Instant = Instant.parse(str)
  }
}
