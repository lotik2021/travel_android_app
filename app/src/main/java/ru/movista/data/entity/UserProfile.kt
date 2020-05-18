package ru.movista.data.entity

data class GetUserProfileResponse(
    val name: String?,
    val last_name: String?,
    val google_transports: List<UserProfileTransportTypesResponse>,
    val favorite_places: List<UserFavoritePlacesResponse>
)

data class UserProfileTransportTypesResponse(
    val google_transport: String,
    val transport_name: String,
    val filter_status: Boolean,
    val icon_name: String,
    val android_icon_url: String,
    val ios_icon_url: String
)

data class UserFavoritePlacesResponse(
    val id: Long,
    val name: String,
    val favorite_place_type: String,
    val updated_at: String,
    val google_place: FavoriteGooglePlaceResponse
)

data class FavoriteGooglePlaceResponse(
    val main_text: String,
    val secondary_text: String,
    val google_place_id: String,
    val coordinate: LocationResponse,
    val types: List<String>?,
    val android_icon_url: String,
    val ios_icon_url_dark: String
)

data class UpdateUserTransportsResponse(
    val data: String
)

data class UpdateUserTransportsRequest(
    val transports: List<UpdateUserTransportsListRequest>
)

data class UpdateUserTransportsListRequest(
    val status: Boolean,
    val name: String
)

data class CreateUserFavoritesResponse(
    val id: Long,
    val error: ApiError? = null
)