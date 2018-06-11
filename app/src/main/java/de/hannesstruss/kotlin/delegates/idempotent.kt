package de.hannesstruss.kotlin.delegates

import kotlin.reflect.KProperty

class IdempotentProperty<T>(initial: T, private val callback: (T) -> Unit) {
  private var field: T = initial

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
    return field
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) {
    if (t != field) {
      field = t
      callback(t)
    }
  }
}
fun <T> idempotent(initial: T, callback: (T) -> Unit) = IdempotentProperty<T>(initial, callback)

class NullableIdempotentProperty<T>(initial: T? = null, private val callback: (T?) -> Unit) {
  private var field: T? = initial

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
    return field
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T?) {
    if (t != field) {
      field = t
      callback(t)
    }
  }
}
fun <T> idempotent(callback: (T?) -> Unit) = NullableIdempotentProperty<T?>(null, callback)
