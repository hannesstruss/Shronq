package de.hannesstruss.shronq.data.fit

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import de.hannesstruss.shronq.ui.di.ActivityHolder
import timber.log.Timber
import javax.inject.Inject

class FitClient @Inject constructor(private val activityProvider: ActivityHolder) {
  companion object {
    private const val REQUEST_CODE_AUTH = 12453
  }

  private var client: GoogleApiClient? = null

  fun connect() {
    getClient().connect()
  }

  fun disconnect() {
    getClient().disconnect()
  }

  fun onActivityResult(requestCode: Int, resultCode: Int) {
    if (requestCode == REQUEST_CODE_AUTH && resultCode == Activity.RESULT_OK) {
      getClient().connect()
    }
  }

  private fun getClient(): GoogleApiClient {
    client?.let { return it }

    createClient().let {
      client = it
      return it
    }
  }

  private fun createClient(): GoogleApiClient {
    return GoogleApiClient.Builder(activityProvider.requireActivity())
        .addApi(Fitness.HISTORY_API)
        .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
        .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
          override fun onConnectionSuspended(status: Int) {
            Timber.d("suspended")
          }

          override fun onConnected(p0: Bundle?) {
            Timber.d("connected")
          }
        })
        .addOnConnectionFailedListener { result ->
          if (result.hasResolution()) {
            Timber.d("Failed, but got resolution. Starting that.")
            result.startResolutionForResult(activityProvider.requireActivity(), REQUEST_CODE_AUTH)
          } else {
            Timber.d("Failed without resolution.")
          }
        }
        .build()
  }
}
