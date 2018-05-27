package de.hannesstruss.shronq.di

import android.app.Application
import dagger.Component
import de.hannesstruss.shronq.ui.base.ViewModelFactory
import de.hannesstruss.shronq.ui.home.HomeFragment
import de.hannesstruss.shronq.ui.logweight.LogWeightFragment
import javax.inject.Singleton

@Component(
    modules = [AppModule::class]
)
@Singleton
interface AppComponent {
  companion object {
    fun init(app: Application): AppComponent {
      return DaggerAppComponent.builder()
          .appModule(AppModule(app))
          .build()
    }
  }

  fun viewModelFactory(): ViewModelFactory

  fun inject(homeFragment: HomeFragment)
  fun inject(homeFragment: LogWeightFragment)
}
