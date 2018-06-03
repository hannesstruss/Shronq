package de.hannesstruss.shronq.data.fit

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import de.hannesstruss.android.activityholder.ActivityHolder
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class FitClient @Inject constructor(private val activityHolder: ActivityHolder) {
  companion object {
    private const val REQUEST_CODE_AUTH = 12453

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_WRITE)
        .build()
  }

  private val context by lazy { activityHolder.activity().applicationContext }

  val isEnabled: Boolean get() = GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)

  fun enable(): Single<ConnectResult> {
    if (isEnabled) {
      return Single.just(ConnectResult.Success)
    }

    val connectTask = Completable.fromCallable {
      val activity = activityHolder.activity()

      GoogleSignIn.requestPermissions(
          activity,
          REQUEST_CODE_AUTH,
          GoogleSignIn.getLastSignedInAccount(activity),
          fitnessOptions
      )
    }

    return connectTask.andThen(
        activityHolder.activityResults
            .doOnNext { Timber.d("I GOT THE RESULT: ${it.resultCode}") }
            .filter { it.requestCode == REQUEST_CODE_AUTH }
            .firstOrError()
            .map {
              when (it.resultCode) {
                Activity.RESULT_OK -> ConnectResult.Success
                Activity.RESULT_CANCELED -> ConnectResult.Canceled
                else -> ConnectResult.Failed
              }
            }
    )
  }

  fun disable(): Completable {
    return Completable.create { emitter ->
      GoogleSignIn.getClient(activityHolder.activity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
          .signOut()
          .addOnCompleteListener {
            emitter.onComplete()
          }
          .addOnFailureListener {
            emitter.onError(it)
          }
          .addOnCanceledListener {
            emitter.onError(RuntimeException("Canceled"))
          }
    }
  }
}
