package de.hannesstruss.shronq.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 2,
    entities = [DbMeasurement::class]
)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun dbMeasurementDao(): DbMeasurementDao
}
