package ru.movista.data.entity.tickets

import ru.movista.data.entity.tickets.common.Error
import ru.movista.data.entity.tickets.common.RouteEntity
import ru.movista.data.entity.tickets.common.ServicesProp
import ru.movista.data.entity.tickets.common.TripsProp

data class SegmentRoutesRequest(
    val uid: String,
    val selectedRouteIds: List<String>,
    val groupId: String,
    val segmentId: String
)

data class SegmentRoutesResponse(
    val data: SegmentRoutesData,
    val errors: List<Error>?
)

data class SegmentRoutesData(
    val trips: Map<String, TripsProp>,
    val services: Map<String, ServicesProp>,
    val routes: List<RouteEntity> = emptyList(),
    val groupId: String,
    val segmentId: String
)