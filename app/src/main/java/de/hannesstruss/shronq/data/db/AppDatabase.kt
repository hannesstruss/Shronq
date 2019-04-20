package de.hannesstruss.shronq.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 6,
    entities = [DbMeasurement::class]
)
@TypeConverters(InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun dbMeasurementDao(): DbMeasurementDao
}
