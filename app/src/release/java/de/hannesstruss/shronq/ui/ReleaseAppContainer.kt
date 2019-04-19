package de.hannesstruss.shronq.ui

import android.app.Activity
import android.view.ViewGroup

class ReleaseAppContainer : AppContainer {
  override fun get(activity: Activity): ViewGroup {
    return activity.findViewById(android.R.id.content)
  }
}
