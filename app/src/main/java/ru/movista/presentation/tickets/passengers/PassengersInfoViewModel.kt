package ru.movista.presentation.tickets.passengers

import java.io.Serializable

data class PassengersInfoViewModel(
    val adultCount: Int,
    val childrenInfo: List<ChildInfoViewModel>
)

data class ChildInfoViewModel(
    val age: Int,
    val seatRequired: Boolean,
    val ageLabel: String,
    val seatLabel: String
) : Serializable