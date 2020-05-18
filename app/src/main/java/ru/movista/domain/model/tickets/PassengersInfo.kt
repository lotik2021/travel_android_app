package ru.movista.domain.model.tickets

data class PassengersInfo(
    val passengers: ArrayList<Passenger>,
    var maxCountReached: Boolean,
    var isMinAdultCount: Boolean,
    var isMinChildCount: Boolean
)