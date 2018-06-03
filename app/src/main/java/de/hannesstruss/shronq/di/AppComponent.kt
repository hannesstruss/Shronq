package de.hannesstruss.shronq.di

import android.app.Application
import dagger.Component
import de.hannesstruss.shronq.ui.di.ActivityComponent
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

  fun activityComponent(): ActivityComponent.Builder
}