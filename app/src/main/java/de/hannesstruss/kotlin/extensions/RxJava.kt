package de.hannesstruss.kotlin.extensions

import io.reactivex.Observable

inline fun <reified R> Observable<*>.ofType(): Observable<R> {
  return filter { it is R }.cast(R::class.java)
}
