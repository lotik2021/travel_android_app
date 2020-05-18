package ru.movista.presentation.tickets.segments

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.model.tickets.Segment
import ru.movista.domain.model.tickets.TripPlace

@Parcelize
class SegmentArgumentsWrapper(
    val searchParams: SearchModel,
    val routes: List<Route>,
    var route: Route? =  null,
    val tripPlaces: Map<Long, TripPlace>,
    val isReturn: Boolean,
    val segments: List<Segment>,
    val searchUid: String,
    val groupId: String,
    val isSkipBasket: Boolean = false
) : Parcelable