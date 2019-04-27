package de.hannesstruss.shronq.ui.s3settings

import android.content.SharedPreferences
import androidx.core.content.edit
import de.hannesstruss.android.KeyboardHider
import de.hannesstruss.shronq.ui.base.MviViewModel
import de.hannesstruss.shronq.ui.navigation.Navigator
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.AccessKeyChanged
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.BucketChanged
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.Save
import de.hannesstruss.shronq.ui.s3settings.S3SettingsIntent.SecretKeyChanged
import javax.inject.Inject

class S3SettingsViewModel
@Inject constructor(
    private val prefs: SharedPreferences,
    private val navigator: Navigator,
    private val keyboardHider: KeyboardHider
) : MviViewModel<S3SettingsState, S3SettingsIntent>() {
  companion object {
    private const val KeyAccessKey = "s3_access_key"
    private const val KeySecretKey = "s3_secret_key"
    private const val KeyBucket = "s3_bucket"
  }

  override val initialState = S3SettingsState.initial()

  override val engine = createEngine {
    onInit {
      enterState {
        state.copy(
            accessKey = prefs.getString(KeyAccessKey, "") ?: "",
            secretKey = prefs.getString(KeySecretKey, "") ?: "",
            bucket = prefs.getString(KeyBucket, "") ?: ""
        )
      }
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

    on<Save> {
      keyboardHider.hideKeyboard()

      val state = getLatestState()
      prefs.edit {
        putString(KeyAccessKey, state.accessKey)
        putString(KeySecretKey, state.secretKey)
        putString(KeyBucket, state.bucket)
      }

      navigator.back()
    }
  }
}
