package de.hannesstruss.shronq.ui.settings

import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import de.hannesstruss.shronq.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.settings_fragment.btn_s3_settings
import kotlinx.android.synthetic.main.settings_fragment.switch_connect_google_fit
import shronq.statemachine.StateMachineFragment

class SettingsFragment : StateMachineFragment<SettingsState, SettingsIntent, SettingsViewModel>() {
  override val layout = R.layout.settings_fragment
  override val viewModelClass = SettingsViewModel::class.java

  override fun intents() = Observable.merge(
      switch_connect_google_fit.checkedChanges()
          .skip(1)
          .map<SettingsIntent> { if (it) SettingsIntent.ConnectFit else SettingsIntent.DisconnectFit },

      btn_s3_settings.clicks().map { SettingsIntent.GoToS3Settings }
  )

  override fun render(state: SettingsState) {
    switch_connect_google_fit.isChecked = state.fitIsEnabled
  }
}
