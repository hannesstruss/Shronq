
buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath(deps.android.gradlePlugin)
    classpath(deps.kotlin.gradlePlugin)
  }
}

allprojects {
  repositories {
    google()
    jcenter()
  }
}
