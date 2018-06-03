package de.hannesstruss.shronq.ui.settings

import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.widget.checkedChanges
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import kotlinx.android.synthetic.main.settings_fragment.switch_connect_google_fit

class SettingsFragment : BaseFragment<SettingsState, SettingsIntent, SettingsEffect, SettingsViewModel>() {
  override val layout = R.layout.settings_fragment
  override val viewModelClass = SettingsViewModel::class.java

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  }

  override fun intents() = switch_connect_google_fit.checkedChanges()
      .map<SettingsIntent> { SettingsIntent.Connect }

  override fun render(state: SettingsState) {

  }

  override fun handleEffect(effect: SettingsEffect) {
  }
}
