package de.hannesstruss.shronq.data

inline class Weight(val grams: Int) : Comparable<Weight> {
  companion object {
    fun fromGrams(grams: Int): Weight = Weight(grams)
  }

  val kilograms: Double get() = grams / 1000.0

  override fun compareTo(other: Weight): Int {
    return grams.compareTo(other.grams)
  }
}
