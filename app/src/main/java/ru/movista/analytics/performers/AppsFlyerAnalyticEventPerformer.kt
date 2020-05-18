package ru.movista.analytics.performers

import com.appsflyer.AppsFlyerLib
import ru.movista.App
import ru.movista.analytics.ActionAnalyticEvent
import ru.movista.analytics.ActionWithDataAnalyticEvent
import ru.movista.analytics.AnalyticEvent
import ru.movista.analytics.AnalyticEventPerformer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppsFlyerAnalyticEventPerformer @Inject constructor(private val app: App) : AnalyticEventPerformer {

    companion object {
        private const val SDK_KEY = "mzhwWQqDdQYMUwDXqstYME"
    }

    init {
        AppsFlyerLib.getInstance().startTracking(app, SDK_KEY)
        AppsFlyerLib.getInstance().setDebugLog(false)
    }

    override fun perform(event: AnalyticEvent) {
        when (event) {
            is ActionAnalyticEvent -> AppsFlyerLib.getInstance().trackEvent(app, event.eventName, null)
            is ActionWithDataAnalyticEvent -> AppsFlyerLib.getInstance().trackEvent(app, event.eventName, event.data)
        }

    }
}