package de.hannesstruss.shronq.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import java.time.Instant

@Dao
interface DbMeasurementDao {
  @Query("select * from dbmeasurement order by measuredAt asc")
  fun selectAll(): Flowable<List<DbMeasurement>>

  @Query("select * from dbmeasurement order by measuredAt desc limit 1")
  fun selectLatest(): Flowable<DbMeasurement>

  @Query("select * from dbmeasurement where id = :id limit 1")
  suspend fun selectById(id: Int): DbMeasurement?

  @Query("select id from dbmeasurement where firebaseId = :firebaseId limit 1")
  suspend fun getIdForFirebaseId(firebaseId: String): Int?

  @Query("select * from dbmeasurement where firebaseId = null or isSynced = 0")
  suspend fun getUnsyncedMeasurements(): List<DbMeasurement>

  @Query("select avg(weightGrams) from dbmeasurement where measuredAt >= :from and measuredAt < :to")
  suspend fun getAverageWeightBetween(from: Instant, to: Instant): Int?

  @Insert
  suspend fun insertAll(vararg measurement: DbMeasurement)

  @Update
  suspend fun update(measurement: DbMeasurement)
}
