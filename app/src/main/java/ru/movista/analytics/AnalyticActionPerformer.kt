package ru.movista.analytics

interface AnalyticEventPerformer {
    fun perform(event: AnalyticEvent)
}