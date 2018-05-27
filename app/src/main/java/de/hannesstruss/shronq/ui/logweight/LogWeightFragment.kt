package de.hannesstruss.shronq.ui.logweight

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.EditText
import de.hannesstruss.shronq.R

class LogWeightFragment : Fragment() {
//  override val layout = R.layout.log_weight_fragment
//
//  override fun onInject(appComponent: AppComponent) {
//    appComponent.inject(this)
//  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val txtView = view.findViewById<EditText>(R.id.edit_weight)

//    view.findViewById<Button>(R.id.btn_insert).setOnClickListener {
//      repo.insertMeasurement(txtView.text.toString().toDouble())
//    }
//
//    repo.getMeasurements().subscribe {
//      Timber.d("Got ${it.size} measurements")
//    }
  }
}
