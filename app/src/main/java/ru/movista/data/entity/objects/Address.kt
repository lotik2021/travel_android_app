package ru.movista.data.entity.objects

import ru.movista.data.entity.SearchPlaceResponseItem

data class AddressesDataEntity(
    val addresses: List<SearchPlaceResponseItem>,
    val token: String
)

data class AddressEntity(
    val main_text: String,
    val place_id: String,
    val secondary_text: String,
    val types: List<String> // health, street_address, geocode etc...
)