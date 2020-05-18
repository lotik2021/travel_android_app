package ru.movista.data.entity

data class ApiError(
    val code: String,
    override val message: String
) : Error()