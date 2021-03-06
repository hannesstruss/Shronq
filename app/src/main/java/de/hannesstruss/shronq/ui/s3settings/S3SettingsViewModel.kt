package de.hannesstruss.shronq.ui.s3settings

import de.hannesstruss.android.KeyboardHider
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.s3sync.BackupToS3Worker
import de.hannesstruss.shronq.data.s3sync.S3Syncer
import de.hannesstruss.shronq.data.s3sync.S3CredentialsStore
import shronq.statemachine.StateMachineViewModel
import de.hannesstruss.shronq.ui.navigation.Navigator
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.AccessKeyChanged
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.BucketChanged
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.EnableSync
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.Import
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.RunSync
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.Save
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.SecretKeyChanged
import javax.inject.Inject

class S3SettingsViewModel
@Inject constructor(
    private val s3CredentialsStore: S3CredentialsStore,
    private val navigator: Navigator,
    private val keyboardHider: KeyboardHider,
    private val s3Syncer: S3Syncer
) : StateMachineViewModel<S3SettingsState, S3SettingsIntent>() {


  override val initialState = S3SettingsState.initial()

  override val engine = createEngine {
    onInit {
      val isScheduled = BackupToS3Worker.isScheduled()
      enterState {
        state.copy(
            deviceName = s3CredentialsStore.deviceName,
            accessKey = s3CredentialsStore.accessKey,
            secretKey = s3CredentialsStore.secretKey,
            bucket = s3CredentialsStore.bucket,
            syncEnabled = isScheduled,
            lastSyncRun = s3Syncer.lastRun
        )
      }
    }

    onDistinct<S3SettingsIntent.DeviceNameChanged> {
      enterState { state.copy(deviceName = it.deviceName) }
    }

    onDistinct<BucketChanged> {
      enterState { state.copy(bucket = it.bucket) }
    }

    onDistinct<AccessKeyChanged> {
      enterState { state.copy(accessKey = it.accessKey) }
    }

    onDistinct<SecretKeyChanged> {
      enterState { state.copy(secretKey = it.secretKey) }
    }

    onDistinct<EnableSync> {
      if (it.enable && !BackupToS3Worker.isScheduled()) {
        BackupToS3Worker.schedulePeriodically()
      } else if (!it.enable && BackupToS3Worker.isScheduled()) {
        BackupToS3Worker.unschedule()
      }
    }

    on<RunSync> {
      enterState { state.copy(manualSyncRunning = true) }
      s3Syncer.backup()
      enterState { state.copy(manualSyncRunning = false, lastSyncRun = s3Syncer.lastRun) }
    }

    on<Save> {
      keyboardHider.hideKeyboard()

      val state = getLatestState()
      s3CredentialsStore.setCredentials(
          deviceName = state.deviceName,
          accessKey = state.accessKey,
          secretKey = state.secretKey,
          bucket = state.bucket
      )

      navigator.back()
    }

    on<Import> {
      navigator.navigate(R.id.action_s3SettingsFragment_to_s3ImportFragment)
    }
  }
}
