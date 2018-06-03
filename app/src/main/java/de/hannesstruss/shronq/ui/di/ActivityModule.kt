package de.hannesstruss.shronq.ui.di

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module(
    includes = [

    ]
)
class ActivityModule(activity: AppCompatActivity) {
  private val activityHolder = ActivityHolder.get(activity)

  @Provides fun activityHolder(): ActivityHolder = activityHolder
}
