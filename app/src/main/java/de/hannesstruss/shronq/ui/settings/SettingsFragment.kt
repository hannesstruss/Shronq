package de.hannesstruss.shronq.ui.settings

import android.os.Bundle
import android.view.View
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable

class SettingsFragment : BaseFragment<SettingsState, SettingsIntent, SettingsEffect, SettingsViewModel>() {
  override val layout = R.layout.settings_fragment
  override val viewModelClass = SettingsViewModel::class.java

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  }

  override val intents = Observable.never<SettingsIntent>()

  override fun render(state: SettingsState) {

  }

  override fun handleEffect(effect: SettingsEffect) {
  }
}
