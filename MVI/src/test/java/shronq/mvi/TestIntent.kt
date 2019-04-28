package shronq.mvi

sealed class TestIntent {
  data class Set(val i: Int) : TestIntent()
  object CountUp : TestIntent()
  object CountDown : TestIntent()
}
