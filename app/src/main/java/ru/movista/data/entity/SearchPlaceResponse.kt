package ru.movista.data.entity

data class SearchPlaceResponse(
    val result: List<SearchPlaceResponseItem>,
    val sessionToken: String,
    val error: ApiError?
)

data class SearchPlaceResponseItem(
    val main_text: String,
    val secondary_text: String,
    val place_id: String,
    val types: List<String>,
    val android_icon_url: String,
    val ios_icon_url_dark: String
)

data class GetPlaceByIDResponse(
    val result: GetPlaceByIDDataResponse
)

data class GetPlaceByIDDataResponse(
    val description: String,
    val place_id: String,
    val location: LocationResponse
)