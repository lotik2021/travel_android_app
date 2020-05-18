package ru.movista.data.entity

sealed class LastLocationEvent

data class LastLocationEventSuccess(
    val lat: Double,
    val lon: Double,
    val accuracy: Float
) : LastLocationEvent()

data class LastLocationEventFailure(
    val cause: String
) : LastLocationEvent()
