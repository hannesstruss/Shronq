package de.hannesstruss.shronq.data.db

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
  @Provides @Singleton fun appDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "shronq.sqlite").build()
  }
}
