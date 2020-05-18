package ru.movista.data.entity.objects

import ru.movista.data.entity.FromLocationEntity
import ru.movista.data.entity.GenericDataEntity
import ru.movista.data.entity.ToLocationEntity
import ru.movista.data.source.local.ObjectType

sealed class TripEntity(
    val object_id: String
)

data class SubwayTripEntity(
    val data: SubwayTripDataEntity
) : TripEntity(ObjectType.TRIP_SUBWAY)

data class SubwayTripDataEntity(
    val arrival_stop: String,
    val arrival_time: String,
    val departure_stop: String,
    val departure_time: String,
    val distance: Int,
    val duration: Int,
    val from_location: FromLocationEntity,
    val headsign: String,
    val line_name: String,
    val line_number: String,
    val line_color: String,
    val number_stops: Int,
    val polyline: String,
    val to_location: ToLocationEntity,
    val trip_type: String,
    val generic_data: GenericDataEntity?
)

data class OnFootTripEntity(
    val data: OnFootTripDataEntity
) : TripEntity(ObjectType.TRIP_ON_FOOT)

data class OnFootTripDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_time: String,
    val arrival_time: String,
    val duration: Int,
    val polyline: String,
    val distance: Int
)

data class BusTripEntity(
    val data: BusTripDataEntity
) : TripEntity(ObjectType.TRIP_BUS)

data class BusTripDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_stop: String,
    val departure_time: String,
    val arrival_time: String,
    val arrival_stop: String,
    val duration: Int,
    val polyline: String,
    val distance: Int,
    val line_number: String,
    val number_stops: Int,
    val generic_data: GenericDataEntity?
)

data class TramTripEntity(
    val data: TramTripDataEntity
) : TripEntity(ObjectType.TRIP_TRAM)

data class TramTripDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_stop: String,
    val departure_time: String,
    val arrival_time: String,
    val arrival_stop: String,
    val duration: Int,
    val polyline: String,
    val distance: Int,
    val line_number: String,
    val number_stops: Int,
    val line_name: String,
    val generic_data: GenericDataEntity?
)

data class HeavyRailTripEntity(
    val data: HeavyRailTripDataEntity
) : TripEntity(ObjectType.TRIP_HEAVY_RAIL)

data class HeavyRailTripDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_stop: String,
    val departure_time: String,
    val arrival_time: String,
    val arrival_stop: String,
    val duration: Int,
    val polyline: String,
    val distance: Int,
    val number_stops: Int,
    val line_name: String,
    val generic_data: GenericDataEntity?
)

data class FerryTripEntity(
    val data: FerryTripDataEntity
) : TripEntity(ObjectType.TRIP_FERRY)

data class FerryTripDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_stop: String,
    val departure_time: String,
    val arrival_time: String,
    val arrival_stop: String,
    val duration: Int,
    val polyline: String,
    val distance: Int,
    val number_stops: Int,
    val line_name: String,
    val generic_data: GenericDataEntity?
)

data class TrolleybusTripEntity(
    val data: TrolleybusTripDataEntity
) : TripEntity(ObjectType.TRIP_TROLLEYBUS)

data class TrolleybusTripDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_stop: String,
    val departure_time: String,
    val arrival_time: String,
    val arrival_stop: String,
    val duration: Int,
    val polyline: String,
    val distance: Int,
    val number_stops: Int,
    val line_number: String,
    val line_name: String,
    val generic_data: GenericDataEntity?
)

data class ShareTaxiTripEntity(
    val data: ShareTaxiTripDataEntity
) : TripEntity(ObjectType.TRIP_SHARE_TAXI)

data class ShareTaxiTripDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_stop: String,
    val departure_time: String,
    val arrival_time: String,
    val arrival_stop: String,
    val duration: Int,
    val polyline: String,
    val distance: Int,
    val number_stops: Int,
    val line_number: String,
    val line_name: String,
    val generic_data: GenericDataEntity?
)

data class CommuterTrainTripEntity(
    val data: CommuterTrainDataEntity
) : TripEntity(ObjectType.TRIP_COMMUTER_TRAIN)

data class CommuterTrainDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val departure_stop: String,
    val departure_time: String,
    val arrival_time: String,
    val arrival_stop: String,
    val duration: Int,
    val polyline: String,
    val distance: Int,
    val trip_type: String,
    val line_name: String,
    val number_stops: Int,
    val trip_short_name: String,
    val generic_data: GenericDataEntity?
)

data class TaxiTripEntity(
    val data: TaxiDataEntity
) : TripEntity(ObjectType.TAXI)




