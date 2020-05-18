package ru.movista.domain.model.tickets

import java.io.Serializable

sealed class Passenger : Serializable {
    abstract val age: Int
    abstract val isSeatRequired: Boolean
}

data class AdultPassenger(
    override val age: Int = 18,
    override var isSeatRequired: Boolean = true
) : Passenger(), Serializable

data class ChildPassenger(
    override var age: Int,
    override var isSeatRequired: Boolean = false
) : Passenger(), Serializable
