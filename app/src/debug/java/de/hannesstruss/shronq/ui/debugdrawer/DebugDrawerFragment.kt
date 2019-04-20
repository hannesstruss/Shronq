package de.hannesstruss.shronq.ui.debugdrawer

import android.view.View
import android.widget.Button
import androidx.annotation.IdRes
import com.jakewharton.rxbinding2.view.clicks
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable

class DebugDrawerFragment : BaseFragment<Unit, DebugDrawerIntent, DebugDrawerViewModel>() {
  override val layout = R.layout.debug_drawer_content
  override val viewModelClass = DebugDrawerViewModel::class.java

  override fun intents() = Observable.merge<DebugDrawerIntent>(
      v<Button>(R.id.btn_test_notification).clicks().map { DebugDrawerIntent.TestNotification },
      v<Button>(R.id.btn_restart_app).clicks().map { DebugDrawerIntent.RestartApp }
  )

  override fun render(state: Unit) {

  }

  private fun <T : View> v(@IdRes id: Int): T {
    return view!!.findViewById(id)
  }
}
