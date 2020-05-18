package ru.movista.domain.usecase.tickets.searchparams

import org.threeten.bp.LocalDate
import ru.movista.domain.model.tickets.TripDates

interface IDateSelectUseCase {

    fun getInitialDates(): TripDates
    fun setTripDate(from: LocalDate, to: LocalDate?)
}