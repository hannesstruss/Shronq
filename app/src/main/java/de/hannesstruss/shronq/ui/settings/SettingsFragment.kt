package de.hannesstruss.shronq.ui.settings

import com.jakewharton.rxbinding2.widget.checkedChanges
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment2
import kotlinx.android.synthetic.main.settings_fragment.switch_connect_google_fit

class SettingsFragment : BaseFragment2<SettingsState, SettingsIntent, SettingsViewModel>() {
  override val layout = R.layout.settings_fragment
  override val viewModelClass = SettingsViewModel::class.java

  override fun intents() = switch_connect_google_fit.checkedChanges()
      .skip(1)
      .map<SettingsIntent> { if (it) SettingsIntent.ConnectFit else SettingsIntent.DisconnectFit }

  override fun render(state: SettingsState) {
    switch_connect_google_fit.isChecked = state.fitIsEnabled
  }
}
