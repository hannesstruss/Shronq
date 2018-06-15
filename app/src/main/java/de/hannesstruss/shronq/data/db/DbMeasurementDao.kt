package de.hannesstruss.shronq.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface DbMeasurementDao {
  @Query("select * from dbmeasurement")
  fun selectAll(): List<DbMeasurement>

  @Insert
  fun insertAll(vararg measurement: DbMeasurement)
}
