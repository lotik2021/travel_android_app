package ru.movista.data.entity


data class ToLocationEntity(
    val latitude: Double,
    val longitude: Double
)

data class FromLocationEntity(
    val latitude: Double,
    val longitude: Double
)

data class FareEntity(
    val currency: String,
    val text: String,
    val value: Int
)

data class AgencyEntity(
    val name: String,
    val phone: String,
    val url: String
)

data class GenericDataEntity(
    val color: String,
    val full_description: String,
    val android_icon_details: String?,
    val icon_details: String?,
    val android_icon_short: String?,
    val icon_short: String?,
    val short_description: String,
    val title: String
)
