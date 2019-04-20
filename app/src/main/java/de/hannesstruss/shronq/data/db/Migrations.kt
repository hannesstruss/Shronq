package de.hannesstruss.shronq.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.transaction

val MIGRATION_2_3 = object : Migration(2, 3) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.transaction {
      db.execSQL("""
        ALTER TABLE dbmeasurement
        ADD COLUMN timezone TEXT
      """.trimIndent())

      db.execSQL("""
        UPDATE dbmeasurement
        SET timezone = rtrim(substr(measuredAt, instr(measuredAt, "[") + 1), "]")
      """.trimIndent())
    }
  }
}
