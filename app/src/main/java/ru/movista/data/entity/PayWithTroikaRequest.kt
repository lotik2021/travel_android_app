package ru.movista.data.entity

data class PayWithTroikaRequest(
    val amount: Int,
    val card_number: String,
    val user_id: String
)