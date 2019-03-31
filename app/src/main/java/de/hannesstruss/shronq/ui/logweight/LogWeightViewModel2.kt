package de.hannesstruss.shronq.ui.logweight

//class LogWeightViewModel2
//@Inject constructor(
//    private val measurementRepository: MeasurementRepository,
//    private val fitClient: FitClient,
//    private val keyboardHider: KeyboardHider
//) : MviViewModel2<LogWeightState, LogWeightIntent>() {
//  override val engine = engineContext {
//    on<LogWeight> {
//      keyboardHider.hideKeyboard()
//
//      enterState { state.copy(isInserting = true) }
//
//      measurementRepository.insertMeasurement(it.weightGrams).await()
//
//      if (fitClient.isEnabled) {
//        fitClient.insert(it.weightGrams).await()
//      }
//
//      // TODO: navigate back
//    }
//  }
//}



