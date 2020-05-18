package ru.movista.data.entity.tickets

import ru.movista.data.entity.tickets.common.*

class PathGroupRequest(val uid: String, val pathGroupId: String)

class PathGroupResponse(
    val data: PathGroupData,
    val errors: List<Error>?
)

class PathGroupData(
    val trips: Map<String, TripsProp>,
    val services: Map<String, ServicesProp>,
    val segments: List<SegmentEntity>
) : BasePathGroupEntity()
