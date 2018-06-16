package de.hannesstruss.shronq.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(
    indices = [
      Index(value = ["firebaseId"], unique = true)
    ]
)
data class DbMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = NO_ID,
    val weightGrams: Int,
    val measuredAt: ZonedDateTime,
    val firebaseId: String?,
    val isSynced: Boolean
) {
  companion object {
    const val NO_ID = 0
  }
}