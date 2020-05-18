package ru.movista.domain.model

data class UserPlacesHistory(
    val favourites: List<PlaceSearchHistoryItem>,
    val recent: List<PlaceSearchHistoryItem>
)

sealed class PlaceSearchHistoryItem(
    val placeName: String,
    val placeDescription: String,
    val placeLat: Double,
    val placeLon: Double,
    val placeId: String
)

data class FavouritePlace(
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double,
    val id: String,
    val type: FavouritePlacesType,
    val icon: String,
    val iosIcon: String
) : PlaceSearchHistoryItem(name, description, lat, lon, id)

data class RecentPlace(
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double,
    val id: String,
    val types: List<String>?,
    val icon: String,
    val iosIcon: String
) : PlaceSearchHistoryItem(name, description, lat, lon, id)