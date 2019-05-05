package de.hannesstruss.shronq.data

import dagger.Module
import de.hannesstruss.shronq.data.db.DbModule

@Module(
    includes = [
      DbModule::class
    ]
)
class DataModule
