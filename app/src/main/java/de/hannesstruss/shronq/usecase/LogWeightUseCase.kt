package de.hannesstruss.shronq.usecase

import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.data.Weight
import de.hannesstruss.shronq.data.fit.FitClient
import de.hannesstruss.shronq.widget.ShronqWidgetProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class LogWeightUseCase
@Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val fitClient: FitClient,
    private val widgetUpdater: ShronqWidgetProvider.Updater
) {
  suspend fun execute(weight: Weight) = coroutineScope {
    val insertDb = async {
      measurementRepository.insertMeasurement(weight)
    }

    val insertFit = async {
      if (fitClient.isEnabled) {
        fitClient.insert(weight).await()
      }
    }

    insertDb.join()
    widgetUpdater.update()
    insertFit.join()
  }
}
