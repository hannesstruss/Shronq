package de.hannesstruss.shronq.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import de.hannesstruss.kotlin.extensions.awaitFirst
import de.hannesstruss.shronq.R
import de.hannesstruss.shronq.data.MeasurementRepository
import de.hannesstruss.shronq.di.AppGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ShronqWidgetProvider : AppWidgetProvider(), CoroutineScope {
  companion object {
    private const val FORCE_UPDATE_WIDGET_IDS = "ShronqWidgetProvider.FORCE_UPDATE_WIDGET_IDS"

    fun updateAll(context: Context) {
      val appWidgetManager = AppWidgetManager.getInstance(context)
      val componentName = ComponentName(context, ShronqWidgetProvider::class.java)
      val ids: IntArray = appWidgetManager.getAppWidgetIds(componentName)

      val intent = Intent(context, ShronqWidgetProvider::class.java)
      intent.putExtra(FORCE_UPDATE_WIDGET_IDS, ids)

      context.sendBroadcast(intent)
    }
  }

  interface Updater {
    fun update()
  }

  class RealUpdater(private val context: Context) : Updater {
    override fun update() {
      updateAll(context)
    }
  }

  @Inject lateinit var measurementRepository: MeasurementRepository

  private val job = Job()
  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  override fun onReceive(context: Context, intent: Intent) {
    val forceUpdateIds = intent.getIntArrayExtra(FORCE_UPDATE_WIDGET_IDS)
    if (forceUpdateIds != null) {
      onUpdate(context, AppWidgetManager.getInstance(context), forceUpdateIds)
    } else {
      super.onReceive(context, intent)
    }
  }

  override fun onUpdate(context: Context,
                        appWidgetManager: AppWidgetManager,
                        appWidgetIds: IntArray) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)

    AppGraph.get(context).inject(this)

    for (widgetId in appWidgetIds) {
      val views = RemoteViews(context.packageName, R.layout.widget)
      launch {
        val weight = measurementRepository.getLatestMeasurement().awaitFirst().weight.kilograms
        views.setTextViewText(R.id.txt_last_weight, String.format("%.1f", weight))
        appWidgetManager.updateAppWidget(widgetId, views)
      }
    }
  }

  override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
    super.onDeleted(context, appWidgetIds)
    job.cancel()
  }
}
