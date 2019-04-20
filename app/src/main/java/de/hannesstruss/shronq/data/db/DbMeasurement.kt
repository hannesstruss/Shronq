package de.hannesstruss.shronq.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

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
    val timezone: String? = null,
    val firebaseId: String?,
    val isSynced: Boolean
) {
  companion object {
    const val NO_ID = 0
  }
}
