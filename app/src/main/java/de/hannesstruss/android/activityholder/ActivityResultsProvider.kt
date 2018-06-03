package de.hannesstruss.android.activityholder

import io.reactivex.Observable

interface ActivityResultsProvider {
  fun activityResults(): Observable<ActivityResult>
}
