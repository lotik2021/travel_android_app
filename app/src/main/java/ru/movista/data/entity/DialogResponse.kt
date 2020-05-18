package ru.movista.data.entity

data class DialogResponse(
    val actions: List<ActionEntity>?,
    val objects: List<ObjectEntity>?,
    val session: SessionEntity?,
    val hint: String?,
    val extras: ExtrasEntity?,
    val error: ApiError?
)