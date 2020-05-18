package ru.movista.data.entity

data class PayWithStrelkaRequest(
    val user_id: String,
    val card_number: String,
    val card_type_id: String,
    val amount: Int
)