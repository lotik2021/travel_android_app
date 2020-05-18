package ru.movista.analytics

sealed class AnalyticEvent(
    val eventName: String
)

data class OpenScreenAnalyticEvent(val screen: String) : AnalyticEvent(screen)

data class ActionAnalyticEvent(
    val name: String
) : AnalyticEvent(name)

data class ActionWithDataAnalyticEvent(
    val name: String,
    val data: Map<String, Any>
) : AnalyticEvent(name)

data class TestEvent(val name: String) : AnalyticEvent(name)