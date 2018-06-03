package de.hannesstruss.shronq.data.fit

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import de.hannesstruss.android.activityholder.ActivityHolder
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit
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

  fun insert(weightGrams: Int): Completable {
    val activity = activityHolder.activity()

    val source = DataSource.Builder()
        .setDataType(DataType.TYPE_WEIGHT)
        .setType(DataSource.TYPE_RAW)
        .setAppPackageName(activity)
        .build()

    val dataset = DataSet.create(source)
    val dataPoint = dataset.createDataPoint()

    dataPoint.setTimestamp(Date().time, TimeUnit.MILLISECONDS)
    dataPoint.getValue(Field.FIELD_WEIGHT).setFloat(weightGrams / 1000f)

    dataset.add(dataPoint)

    return Completable.create { emitter ->
      val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity)!!
      Fitness.getHistoryClient(activity, lastSignedInAccount).insertData(dataset)
          .addOnFailureListener { emitter.onError(it) }
          .addOnCompleteListener { emitter.onComplete() }
    }
  }
}
