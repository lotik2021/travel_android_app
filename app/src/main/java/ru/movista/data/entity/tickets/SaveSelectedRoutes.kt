package ru.movista.data.entity.tickets

import ru.movista.data.entity.tickets.common.Error

class SaveSelectedRoutesRequest(
    val uid: String,
    val selectedRouteIds: List<String>,
    val groupId: String
)

class SaveSelectedRoutesResponse(
    val data: SaveSelectedRoutesData?,
    val errors: List<Error>?
)

class SaveSelectedRoutesData(
    val uid: String?,
    val bookingUrl: String?,
    var actualUrl: String? = null
)
