package ru.movista.data.entity

data class PingLocationRequest(
    val location: LocationRequest,
    val user_id: String
)

data class LocationRequest(
    val latitude: Double,
    val longitude: Double
)