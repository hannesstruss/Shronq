package de.hannesstruss.shronq.ui.di

import dagger.Module
import dagger.Provides
import de.hannesstruss.android.activityholder.ActivityHolder
import de.hannesstruss.shronq.ui.MainActivity

@Module
class ActivityModule(activity: MainActivity) {
  private val activityHolder = ActivityHolder.get(activity)

  @Provides fun activityHolder(): ActivityHolder = activityHolder
}
