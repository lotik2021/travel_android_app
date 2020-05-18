package ru.movista.presentation.viewmodel

data class WeatherViewModel(
    val temperature: Int,
    val description: String,
    val pressure: Long,
    val humidity: Long,
    val windSpeed: String,
    val iconUrl: String?,
    val iosIconUrl: String?
)