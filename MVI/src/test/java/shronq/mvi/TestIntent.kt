package shronq.mvi

sealed class TestIntent {
  object CountUp : TestIntent()
  object CountDown : TestIntent()
}
