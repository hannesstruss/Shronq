package de.hannesstruss.shronq.ui.s3settings

import com.jakewharton.rxbinding2.widget.textChanges
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.s3settings_fragment.edit_bucket

class S3SettingsFragment : BaseFragment<S3SettingsState, S3SettingsIntent, S3SettingsViewModel>() {
  override val layout = R.layout.s3settings_fragment

  override val viewModelClass = S3SettingsViewModel::class.java

  override fun intents(): Observable<S3SettingsIntent> {
    return edit_bucket.textChanges()
//        .map { it.toString() }
//        .distinctUntilChanged()
        .map { S3SettingsIntent.BucketChanged(it.toString()) }
  }

  override fun render(state: S3SettingsState) {
    if (edit_bucket.text.toString() != state.bucket) {
      edit_bucket.setText(state.bucket)
    }
  }
}
