@file:Suppress("ClassName", "unused")

object deps {
  object kotlin {
    private const val kotlinVersion = "1.2.41"

    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
  }

  object android {
    object build {
      const val buildToolsVersion = "27.0.3"
      const val compileSdkVersion = 27
      const val minSdkVersion = 26
      const val targetSdkVersion = 27
    }

    private const val supportLibVersion = "27.1.1"

    const val gradlePlugin = "com.android.tools.build:gradle:3.2.0-alpha15"
    const val appCompat = "com.android.support:appcompat-v7:$supportLibVersion"
    const val constraintLayout = "com.android.support.constraint:constraint-layout:1.1.0"
  }
}
