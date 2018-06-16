package de.hannesstruss.shronq.data

import dagger.Module
import de.hannesstruss.shronq.data.db.DbModule
import de.hannesstruss.shronq.data.firebase.FirebaseModule

@Module(
    includes = [
      DbModule::class,
      FirebaseModule::class
    ]
)
class DataModule
