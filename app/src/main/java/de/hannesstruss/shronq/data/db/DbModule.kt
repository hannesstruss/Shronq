package de.hannesstruss.shronq.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
  @Provides @Singleton fun appDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
        .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
        .build()
  }

  @Provides fun measurementDao(db: AppDatabase): DbMeasurementDao {
    return db.dbMeasurementDao()
  }
}
