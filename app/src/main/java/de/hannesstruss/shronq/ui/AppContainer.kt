package de.hannesstruss.shronq.ui

import android.app.Activity
import android.view.ViewGroup

interface AppContainer {
  fun get(activity: Activity): ViewGroup
}
