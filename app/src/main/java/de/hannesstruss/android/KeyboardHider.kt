package de.hannesstruss.android

import android.view.View
import android.view.inputmethod.InputMethodManager
import de.hannesstruss.android.activityholder.ActivityHolder
import javax.inject.Inject

class KeyboardHider @Inject constructor(
    private val activityHolder: ActivityHolder
) {
  fun hideKeyboard() {
    val activity = activityHolder.activity()
    val imm = activity.getSystemService(InputMethodManager::class.java)
    val view = activity.currentFocus ?: View(activity)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }
}
