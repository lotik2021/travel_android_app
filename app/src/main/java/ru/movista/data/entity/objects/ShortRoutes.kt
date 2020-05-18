package ru.movista.data.entity.objects

import ru.movista.data.entity.AgencyEntity
import ru.movista.data.entity.FareEntity
import ru.movista.data.entity.FromLocationEntity
import ru.movista.data.entity.ToLocationEntity
import ru.movista.data.source.local.ObjectType

data class ShortRoutesDataEntity(
    val routes: List<ShortRouteEntity>
)

sealed class ShortRouteEntity(
    val object_id: String
)

data class GoogleRouteEntity(
    val data: GoogleRouteDataEntity,
    val id: String
) : ShortRouteEntity(ObjectType.ROUTE_GOOGLE)

data class GoogleRouteDataEntity(
    val distance: Int,
    val duration: Int,
    val start_time: String,
    val end_time: String,
    val fare: FareEntity?,
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val from_address: String,
    val to_address: String,
    val polyline: String,
    val trips: List<TripEntity>,
    val agencies: List<AgencyEntity>?
)
