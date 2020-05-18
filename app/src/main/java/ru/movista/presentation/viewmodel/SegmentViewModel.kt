package ru.movista.presentation.viewmodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.movista.data.source.local.models.BasketItemState
import ru.movista.data.source.local.models.TripType

@Parcelize
class SegmentViewModel(
    val id: String,
    var title: String,
    var description: String,
    var subDescription: String,
    var tripTypes: List<TripType>,
    var isReturnTrip: Boolean,
    var basketItemState: BasketItemState
) : Parcelable