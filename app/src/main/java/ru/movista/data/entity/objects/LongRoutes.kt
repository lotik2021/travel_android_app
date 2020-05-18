package ru.movista.data.entity.objects

import ru.movista.data.source.local.ObjectType

data class LongRoutesDataEntity(
    val routes: List<LongRouteEntity>
)

sealed class LongRouteEntity(
    val object_id: String
)

data class MovistaRouteEntity(
    val data: MovistaRouteDataEntity,
    val id: String
) : LongRouteEntity(ObjectType.ROUTE_MOVISTA)

data class MovistaRouteDataEntity(
    val departure_time: String,
    val arrival_time: String,
    val duration: Int,
    val redirect_url: String,
    val number_of_transfers: Int,
    val trips: List<String>,
    val price: Float
)

