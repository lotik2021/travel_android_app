package ru.movista.domain.model.tickets

import ru.movista.data.source.local.models.PathGroupState
import ru.movista.data.source.local.models.TripType
import java.io.Serializable

data class TripGroupData(val uid: String, val searchResult: List<TripGroup>)

data class TripGroup(
    val id: String,
    val title: String?,
    val description: String?,
    val minDuration: String?,
    val minDurationTitle: String?,
    val minPrice: Double?,
    val minPriceTitle: String?,
    val tripTypeSequenceIcons: List<List<TripType>>,
    val state: PathGroupState = PathGroupState.UNDEFINED
) : Serializable