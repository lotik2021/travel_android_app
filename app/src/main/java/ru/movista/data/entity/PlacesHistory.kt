package ru.movista.data.entity

import ru.movista.domain.model.FavouritePlacesType

data class FavouritePlacesRequest(
    val user_id: String,
    val count: Int
)

data class FavouritePlacesResponse(
    val result: List<FavouritePlacesResponseResult>
)

data class FavouritePlacesResponseResult(
    val location: HistoryLocationResponse,
    val main_text: String = "",
    val secondary_text: String = "",
    val place_id: String,
    val type: FavouritePlacesType,
    val android_icon_url: String,
    val ios_icon_url_dark: String
)

data class RecentPlacesRequest(
    val user_id: String,
    val count: Int
)

data class RecentPlacesResponse(
    val result: List<RecentPlacesResponseResult>
)

data class RecentPlacesResponseResult(
    val location: HistoryLocationResponse,
    val main_text: String = "",
    val secondary_text: String = "",
    val place_id: String,
    val types: List<String>?,
    val android_icon_url: String,
    val ios_icon_url_dark: String
)

data class HistoryLocationResponse(
    val latitude: Double,
    val longitude: Double
)

data class UserFavoritePlacesRequest(
    val place_id: String,
    val location: HistoryLocationResponse,
    val main_text: String,
    val secondary_text: String,
    val type: String,
    val name: String?
)