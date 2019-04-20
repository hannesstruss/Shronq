package de.hannesstruss.shronq.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
  @Provides @Singleton fun appDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "shronq.sqlite")
        .addMigrations(MIGRATION_2_3)
        .build()
  }

  @Provides fun measurementDao(db: AppDatabase): DbMeasurementDao {
    return db.dbMeasurementDao()
  }
}
