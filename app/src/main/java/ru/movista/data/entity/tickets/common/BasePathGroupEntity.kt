package ru.movista.data.entity.tickets.common

import ru.movista.data.source.local.models.PathGroupState
import ru.movista.data.source.local.models.PriceSegmentType
import ru.movista.data.source.local.models.TripType

open class BasePathGroupEntity {
    val id: String = ""
    val title: String? = ""
    val descr: String? = ""
    val minDuration: String? = ""
    val minDurationTitle: String? = ""
    val minPrice: Double? = -1.0
    val minPriceTitle: String? = ""
    val tripTypes: List<String>? = emptyList()
    val tripTypeSequence: List<List<TripType>> = emptyList()
    val routesCount: Long? = -1
    val bookableStatus: String? = ""
    val places: Map<Long, PlaceEntity>? = emptyMap()
    val placeIds: List<Long>? = emptyList()
    val state: PathGroupState = PathGroupState.UNDEFINED
}

class SegmentEntity(
    val id: String,
    val name: String,
    val fromId: Long,
    val toId: Long,
    val isReturn: Boolean,
    val tripTypes: List<TripType>,
    val routes: List<RouteEntity> = emptyList()
)

class RouteEntity(
    val totalRoutePrice: Double,
    val priceSegmentType: PriceSegmentType,
    val id: String,
    val tripIds: List<String>,
    val uniqueTripIds: List<String>,
    val tripIdToServiceId: List<TripIdToServiceId>,
    val serviceIds: List<String>,
    val descr: String,
    val minAgrPrice: Double,
    val price: Double,
    val routeDuration: Long,
    val isForward: Boolean
)

class TripIdToServiceId(
    val tripId: String,
    val serviceId: String
)

class PlaceEntity(
    val id: Long,
    val name: String?,
    val lat: Double?,
    val lon: Double?,
    val timeZone: String?,
    val countryName: String?,
    val stateName: String?,
    val cityName: String?,
    val stationName: String?,
    val platformName: String?,
    val description: String?,
    val fullName: String?
)