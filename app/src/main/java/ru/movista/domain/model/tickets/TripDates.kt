package ru.movista.domain.model.tickets

import org.threeten.bp.LocalDate

data class TripDates(
    val departureDate: LocalDate?,
    val arrivalDate: LocalDate?
)