package ru.movista.data.entity

data class CreateSessionRequest(
    val session: CreateSessionSessionEntity
)

data class ActionRequest(
    val meta: MetaRequest,
    val action_id: String,
    val session: SessionEntity,
    val client_entities: Any = EmptyClientEntityRequest,
    val user_response: String? = null
)

data class MetaRequest(
    val user_id: String,
    val location: Any = EmptyClientEntityRequest,
    val time_zone: String
)

data class MetaLocationEntityRequest(
    val latitude: Double,
    val longitude: Double
)

data class ActionEntity(
    val handable: Boolean,
    val title: String,
    val action_id: String
)

object EmptyClientEntityRequest

data class LocationClientEntityRequest(
    val location: LocationDataEntityRequest
)

data class TripTimeClientEntityRequest(
    val location: TripTimeDataEntityRequest
)

data class TripTimeDataEntityRequest(
    val trip_time: String
)

data class LocationDataEntityRequest(
    val latitude: Double,
    val longitude: Double,
    val main_text: String = "",
    val secondary_text: String = "",
    val place_id: String = ""
)



