package de.hannesstruss.shronq.ui.s3settings

import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable

class S3SettingsFragment : BaseFragment<S3SettingsState, S3SettingsIntent, S3SettingsViewModel>() {
  override val layout = R.layout.s3settings_fragment

  override val viewModelClass = S3SettingsViewModel::class.java

  override fun intents(): Observable<S3SettingsIntent> {
    return Observable.never()
  }

  override fun render(state: S3SettingsState) {
  }
}
