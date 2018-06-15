package de.hannesstruss.shronq.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface DbMeasurementDao {
  @Query("select * from dbmeasurement")
  fun selectAll(): Flowable<List<DbMeasurement>>

  @Query("select * from dbmeasurement order by measuredAt desc limit 1")
  fun selectLatest(): Flowable<DbMeasurement>

  @Insert
  fun insertAll(vararg measurement: DbMeasurement)
}
