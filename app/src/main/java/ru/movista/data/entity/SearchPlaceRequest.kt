package ru.movista.data.entity

data class SearchPlaceRequest(
    val token: String,
    val region: String = "ru",
    val input: String
)

data class GetPlaceByIDRequest(
    val token: String,
    val region: String = "ru"
)

