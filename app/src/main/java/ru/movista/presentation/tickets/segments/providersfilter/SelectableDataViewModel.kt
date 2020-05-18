package ru.movista.presentation.tickets.segments.providersfilter

import ru.movista.data.source.local.models.ObjectTripType
import ru.movista.presentation.tickets.segments.RouteItemPlaceFilter

sealed class BaseFilterViewModel(
    open val tripType: ObjectTripType
)

data class TitleFilterViewModel(
    val title: String,
    override val tripType: ObjectTripType,
    var isSelected: Boolean
) : BaseFilterViewModel(tripType)

data class ItemFilterViewModel(
    val itemPlaceFilter: RouteItemPlaceFilter,
    override val tripType: ObjectTripType
) : BaseFilterViewModel(tripType)