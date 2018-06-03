package de.hannesstruss.shronq.ui.settings

import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable

class SettingsFragment : BaseFragment<SettingsState, SettingsIntent, SettingsEffect, SettingsViewModel>() {
  override val layout = R.layout.settings_fragment
  override val viewModelClass = SettingsViewModel::class.java

  override val intents = Observable.never<SettingsIntent>()

  override fun render(state: SettingsState) {

  }

  override fun handleEffect(effect: SettingsEffect) {
  }
}
