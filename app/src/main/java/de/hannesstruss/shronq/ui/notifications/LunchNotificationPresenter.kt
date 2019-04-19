package de.hannesstruss.shronq.ui.notifications

import de.hannesstruss.shronq.data.MeasurementRepository
import javax.inject.Inject

class LunchNotificationPresenter
@Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
}
