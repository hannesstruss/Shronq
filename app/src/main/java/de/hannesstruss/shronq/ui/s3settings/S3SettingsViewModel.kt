package de.hannesstruss.shronq.ui.s3settings

import de.hannesstruss.shronq.ui.base.MviViewModel
import javax.inject.Inject

class S3SettingsViewModel
@Inject constructor() : MviViewModel<S3SettingsState, S3SettingsIntent>() {
  override val initialState = S3SettingsState.initial()
  override val engine = createEngine {  }
}
