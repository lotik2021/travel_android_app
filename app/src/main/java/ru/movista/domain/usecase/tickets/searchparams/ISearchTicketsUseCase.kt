package ru.movista.domain.usecase.tickets.searchparams

import ru.movista.domain.model.tickets.SearchModel


interface ISearchTicketsUseCase {

    fun getSearchParams(): SearchModel

    fun swapPlaces()
}