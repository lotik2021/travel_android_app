package ru.movista.domain.model.tickets

import java.io.Serializable

data class Place(
    val id: Long,
    val name: String,
    val description: String,
    val iconName: String
) : Serializable