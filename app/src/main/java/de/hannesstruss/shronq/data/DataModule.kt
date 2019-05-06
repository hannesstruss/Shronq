package de.hannesstruss.shronq.data

import dagger.Module
import de.hannesstruss.shronq.data.db.DbModule
import de.hannesstruss.shronq.data.s3sync.S3SyncModule

@Module(
    includes = [
      DbModule::class,
      S3SyncModule::class
    ]
)
class DataModule
