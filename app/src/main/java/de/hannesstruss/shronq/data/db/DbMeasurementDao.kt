package de.hannesstruss.shronq.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface DbMeasurementDao {
  @Query("select * from dbmeasurement")
  fun selectAll(): Flowable<List<DbMeasurement>>

  @Query("select * from dbmeasurement order by measuredAt desc limit 1")
  fun selectLatest(): Flowable<DbMeasurement>

  @Query("select * from dbmeasurement where id = :id limit 1")
  fun selectById(id: Int): Maybe<DbMeasurement>

  @Query("select id from dbmeasurement where firebaseId = :firebaseId limit 1")
  fun getIdForFirebaseId(firebaseId: String): Int?

  @Query("select * from dbmeasurement where firebaseId = null or isSynced = 0")
  fun getUnsyncedMeasurements(): Single<List<DbMeasurement>>

  @Insert
  fun insertAll(vararg measurement: DbMeasurement)

  @Update
  fun update(measurement: DbMeasurement)
}
