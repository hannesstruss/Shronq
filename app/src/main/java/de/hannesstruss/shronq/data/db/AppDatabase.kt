package de.hannesstruss.shronq.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(
    version = 2,
    entities = [DbMeasurement::class]
)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun dbMeasurementDao(): DbMeasurementDao
}
