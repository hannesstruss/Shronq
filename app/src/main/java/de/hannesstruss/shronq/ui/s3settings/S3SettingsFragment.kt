package de.hannesstruss.shronq.ui.s3settings

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.textChanges
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.ui.base.BaseFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.s3settings_fragment.btn_run_sync
import kotlinx.android.synthetic.main.s3settings_fragment.edit_access_key
import kotlinx.android.synthetic.main.s3settings_fragment.edit_bucket
import kotlinx.android.synthetic.main.s3settings_fragment.edit_device_name
import kotlinx.android.synthetic.main.s3settings_fragment.edit_secret_key
import kotlinx.android.synthetic.main.s3settings_fragment.switch_sync_enabled
import kotlinx.android.synthetic.main.s3settings_fragment.toolbar
import kotlinx.android.synthetic.main.s3settings_fragment.txt_last_run

class S3SettingsFragment : BaseFragment<S3SettingsState, S3SettingsIntent, S3SettingsViewModel>() {
  override val layout = R.layout.s3settings_fragment

  override val viewModelClass = S3SettingsViewModel::class.java

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    toolbar.inflateMenu(R.menu.s3_settings_menu)
  }

  override fun intents(): Observable<S3SettingsIntent> {
    return Observable.mergeArray(
        edit_device_name.textChanges()
            .map { S3SettingsIntent.DeviceNameChanged(it.toString()) },

        edit_access_key.textChanges()
            .map { S3SettingsIntent.AccessKeyChanged(it.toString()) },

        edit_secret_key.textChanges()
            .map { S3SettingsIntent.SecretKeyChanged(it.toString()) },

        edit_bucket.textChanges()
            .map { S3SettingsIntent.BucketChanged(it.toString()) },

        switch_sync_enabled.checkedChanges()
            .map { S3SettingsIntent.EnableSync(it) },

        btn_run_sync.clicks()
            .map { S3SettingsIntent.RunSync },

        toolbar.itemClicks().map { S3SettingsIntent.Save }
    )
  }

  override fun render(state: S3SettingsState) {
    edit_device_name.setTextIfChanged(state.deviceName)
    edit_access_key.setTextIfChanged(state.accessKey)
    edit_secret_key.setTextIfChanged(state.secretKey)
    edit_bucket.setTextIfChanged(state.bucket)
    switch_sync_enabled.setCheckedIfChanged(state.syncEnabled)
    txt_last_run.setTextIfChanged(state.lastSyncRunText)
    btn_run_sync.isEnabled = state.runSyncButtonEnabled
  }

  private fun TextView.setTextIfChanged(newText: String) {
    if (text.toString() != newText) {
      text = newText
    }
  }

  private fun CompoundButton.setCheckedIfChanged(checked: Boolean) {
    if (isChecked != checked) {
      isChecked = checked
    }
  }
}
