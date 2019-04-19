package de.hannesstruss.shronq.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import de.hannesstruss.shronq.R

class DebugAppContainer : AppContainer {
  override fun get(activity: Activity): ViewGroup {
    val content = activity.findViewById<ViewGroup>(android.R.id.content)
    val drawerLayout = LayoutInflater.from(activity).inflate(R.layout.debug_activity_frame, content, true) as ViewGroup
    return drawerLayout.findViewById(R.id.content_root)
  }
}
