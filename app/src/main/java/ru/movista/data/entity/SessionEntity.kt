package ru.movista.data.entity

data class CreateSessionSessionEntity(
    val message_date_time: String
)

data class SessionEntity(
    val message_id: Int,
    val session_id: String,
    val message_date_time: String
)