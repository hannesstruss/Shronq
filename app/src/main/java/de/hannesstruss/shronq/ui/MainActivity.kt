package de.hannesstruss.shronq.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.MeasurementRepository

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    MeasurementRepository()
  }
}
