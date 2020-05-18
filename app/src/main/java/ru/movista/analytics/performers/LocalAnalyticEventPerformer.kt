package ru.movista.analytics.performers

import ru.movista.analytics.AnalyticEvent
import ru.movista.analytics.AnalyticEventPerformer
import timber.log.Timber

object LocalAnalyticEventPerformer : AnalyticEventPerformer {

    override fun perform(event: AnalyticEvent) {
        Timber.tag("Analytics").d("Analytic action: $event")
    }
}