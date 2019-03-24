package de.hannesstruss.shronq.data.db

import androidx.room.TypeConverter
import java.time.ZonedDateTime

class ZonedDateTimeConverter {
  companion object {
    @JvmStatic @TypeConverter fun fromZonedDateTime(zdt: ZonedDateTime): String = zdt.toString()
    @JvmStatic @TypeConverter fun toZonedDateTime(str: String): ZonedDateTime = ZonedDateTime.parse(str)
  }
}
