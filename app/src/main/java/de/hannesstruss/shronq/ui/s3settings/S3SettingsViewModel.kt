package de.hannesstruss.shronq.ui.s3settings

import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.BucketChanged
import javax.inject.Inject

class S3SettingsViewModel
@Inject constructor() : MviViewModel<S3SettingsState, S3SettingsIntent>() {
  override val initialState = S3SettingsState.initial()
  override val engine = createEngine {
    onDistinct<BucketChanged> {
      // TODO should there be onDistinct<BucketChanged>? Or on<bucketChanged>(distinct = true) { ...
      // or we can remove onFirst and pass an optional enum { ALL, DISTINCT, FIRST } to on()
      println("Changed: ${it.bucket}")
      enterState { state.copy(bucket = it.bucket) }
    }
  }
}
