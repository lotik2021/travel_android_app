package ru.movista.domain.usecase.tickets.searchparams

import ru.movista.domain.model.tickets.PassengersInfo

interface IPassengersUseCase {

    fun getInitialPassengersInfoModel(): PassengersInfo
    fun increaseAdultCount()
    fun decreaseAdultCount()
    fun increaseChildrenCount(childAge: Int, seatRequired: Boolean)
    fun decreaseChildrenCount()
    fun notifyNewPassengersInfoConfirmed()
    fun updateChildInfo(index: Int, age: Int, isSeatRequired: Boolean)
    fun isValidChildrenCount(): Boolean
}