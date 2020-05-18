package ru.movista.data.entity

data class AvailableTravelCardsResponse(
    val travel_cards: List<TravelCardEntity>
)

data class TravelCardEntity(
    val id: Int,
    val name: String,
    val description: String,
    val warn: String,
    val minimum_card_length: Int,
    val maximum_card_length: Int,
    val maximum_amount: Int?,
    val minimum_amount: Int?
)