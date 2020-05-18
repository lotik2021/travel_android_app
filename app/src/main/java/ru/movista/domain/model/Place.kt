package ru.movista.domain.model

sealed class UserData

data class Place(
    val name: String = "",
    val description: String = "",
    val lat: Double,
    val lon: Double,
    val placeId: String = ""
) : UserData()

data class TripTime(val tripTime: String) : UserData()