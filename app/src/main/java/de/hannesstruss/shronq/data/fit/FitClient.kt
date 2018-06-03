package de.hannesstruss.shronq.data.fit

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import de.hannesstruss.shronq.ui.di.ActivityHolder
import javax.inject.Inject

class FitClient @Inject constructor(private val activityProvider: ActivityHolder) {
  companion object {
    private const val REQUEST_CODE_AUTH = 12453

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_WRITE)
        .build()
  }

  private val context by lazy { activityProvider.activity().applicationContext }

  private val hasPermissions get() = GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)

  fun connect() {
    if (!hasPermissions) {
      val activity = activityProvider.activity()

      GoogleSignIn.requestPermissions(
          activity,
          REQUEST_CODE_AUTH,
          GoogleSignIn.getLastSignedInAccount(activity),
          fitnessOptions)
    }
  }

  fun onActivityResult(requestCode: Int, resultCode: Int) {
    if (requestCode == REQUEST_CODE_AUTH && resultCode == Activity.RESULT_OK) {

      TODO()
    }
  }

}
