package ru.movista.analytics.performers

import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import ru.movista.App
import ru.movista.BuildConfig
import ru.movista.analytics.ActionAnalyticEvent
import ru.movista.analytics.ActionWithDataAnalyticEvent
import ru.movista.analytics.AnalyticEvent
import ru.movista.analytics.AnalyticEventPerformer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexAnalyticEventPerformer @Inject constructor(context: App) : AnalyticEventPerformer {

    init {
        val config = YandexMetricaConfig
            .newConfigBuilder(BuildConfig.YANDEX_METRICA_API_KEY)
            .build()
        YandexMetrica.activate(context, config)
        YandexMetrica.enableActivityAutoTracking(context)
    }

    override fun perform(event: AnalyticEvent) {
        when (event) {
            is ActionAnalyticEvent -> YandexMetrica.reportEvent(event.eventName)
            is ActionWithDataAnalyticEvent -> YandexMetrica.reportEvent(event.eventName, event.data)
        }
    }
}