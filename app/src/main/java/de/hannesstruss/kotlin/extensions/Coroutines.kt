package de.hannesstruss.kotlin.extensions

import io.reactivex.Flowable
import io.reactivex.Observable
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.reactive.openSubscription
import kotlinx.coroutines.rx2.openSubscription

fun <T> ReceiveChannel<T>.toFlow(): Flow<T> {
  return flow {
    consumeEach {
      emit(it)
    }
  }
}

fun <T> Flowable<T>.toFlow(): Flow<T> {
  val channel = openSubscription()
  return channel.toFlow()
}

fun <T> Observable<T>.toFlow(): Flow<T> {
  val channel = openSubscription()
  return channel.toFlow()
}

suspend fun <T> Flow<T>.awaitFirst(): T {
  var t: T? = null
  take(1).collect { t = it }
  return t!!
}
