package ru.movista.data.entity.tickets

data class SearchPlacesRequest(
    val name: String,
    val count: Int = 20
)

data class SearchPlacesResponse(
    val result: List<PlaceFoundEntity>
)

data class PlaceFoundEntity(
    val id: Long,
    val name: String?,
    val lat: Double,
    val lon: Double,
    val timeZone: String?,
    val countryName: String,
    val stateName: String,
    val cityName: String?,
    val stationName: String?,
    val platformName: String,
    val description: String?,
    val fullName: String?,
    val placeclassId: Int,
    val nearestBiggerPlaceId: Int,
    val typePlace: Int
)
