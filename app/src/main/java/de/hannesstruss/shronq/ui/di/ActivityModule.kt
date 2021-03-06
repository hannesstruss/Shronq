package de.hannesstruss.shronq.ui.di

import androidx.navigation.findNavController
import dagger.Module
import dagger.Provides
import de.hannesstruss.android.activityholder.ActivityHolder
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.MainActivity
import de.hannesstruss.shronq.ui.navigation.Navigator

@Module
class ActivityModule(activity: MainActivity) {
  private val activityHolder = ActivityHolder.get(activity)

  @Provides fun activityHolder(): ActivityHolder = activityHolder

  @Provides fun navigator(): Navigator {
    return object : Navigator {
      private val navController get() =
        activityHolder.activity().findNavController(R.id.nav_host_fragment)

      override fun back() {
        navController.popBackStack()
      }

      override fun navigate(action: Int) {
        navController.navigate(action)
      }
    }
  }
}
