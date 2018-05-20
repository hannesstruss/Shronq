import org.gradle.internal.impldep.com.amazonaws.PredefinedClientConfigurations.defaultConfig
import org.gradle.api.internal.plugins.DefaultConvention
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(deps.android.build.compileSdkVersion)
  buildToolsVersion(deps.android.build.buildToolsVersion)

  defaultConfig {
    applicationId = "de.hannesstruss.shronq"
    minSdkVersion(deps.android.build.minSdkVersion)
    targetSdkVersion(deps.android.build.targetSdkVersion)
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".dev"
      versionNameSuffix = "-dev"
    }
  }
}

dependencies {
  implementation(deps.kotlin.stdlib)
  implementation(deps.android.appCompat)
  implementation(deps.android.constraintLayout)
}
