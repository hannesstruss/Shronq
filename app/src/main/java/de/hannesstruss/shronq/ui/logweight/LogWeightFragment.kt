package de.hannesstruss.shronq.ui.logweight

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.ui.base.BaseFragment
import timber.log.Timber

class LogWeightFragment : BaseFragment() {
  override val layout = R.layout.log_weight_fragment

  val repo = MeasurementRepository()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val txtView = view.findViewById<EditText>(R.id.edit_weight)

    view.findViewById<Button>(R.id.btn_insert).setOnClickListener {
      repo.insertMeasurement(txtView.text.toString().toDouble())
    }

    repo.getMeasurements().subscribe {
      Timber.d("Got ${it.size} measurements")
    }
  }
}
