package ru.movista.data.entity.objects

import ru.movista.data.entity.AgencyEntity
import ru.movista.data.entity.FareEntity
import ru.movista.data.entity.FromLocationEntity
import ru.movista.data.entity.ToLocationEntity
import ru.movista.data.source.local.ObjectType

data class CarDataEntity(
    val routes: List<CarRouteEntity>
)

sealed class CarRouteEntity(
    val object_id: String
)

data class GoogleCarEntity(
    val data: GoogleCarDataEntity,
    val id: String
) : CarRouteEntity(ObjectType.TRIP_CAR)

data class GoogleCarDataEntity(
    val distance: Int,
    val duration: Int,
    val duration_in_traffic: Int,
    val start_time: String,
    val end_time: String,
    val from_address: String,
    val from_location: FromLocationEntity,
    val to_address: String,
    val to_location: ToLocationEntity,
    val polyline: String,
    val deeplinks: List<DeeplinkDataEntity>,
    val agencies: AgencyEntity?,
    val fare: FareEntity?,
    val trips: TripEntity?,
    val summary: String
)

data class DeeplinkDataEntity(
    val android: String,
    val title: String,
    val icon_name: String
)
