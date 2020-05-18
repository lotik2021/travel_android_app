package ru.movista.domain.usecase.tickets.searchparams

import ru.movista.domain.model.tickets.ComfortType

interface IComfortTypeUseCase {

    fun setComfortType(comfortType: ComfortType)
}