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

val MIGRATION_3_4 = object : Migration(3, 4) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.transaction {
      db.execSQL("""
        UPDATE dbmeasurement
        SET measuredAt = substr(measuredAt, 0, instr(measuredAt, "["))
      """.trimIndent())
    }
  }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.transaction {
      db.execSQL("""
        UPDATE dbmeasurement
        SET measuredAt = substr(measuredAt, 0, instr(measuredAt, "+"))
      """.trimIndent())
    }
  }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.transaction {
      db.execSQL("""
        UPDATE dbmeasurement
        SET measuredAt = measuredAt || "Z"
      """.trimIndent())
    }
  }
}
