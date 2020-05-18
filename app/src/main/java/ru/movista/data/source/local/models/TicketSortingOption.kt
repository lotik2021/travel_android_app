package ru.movista.data.source.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.movista.R

@Parcelize
enum class TicketSortingOption(val titleResId: Int) : Parcelable {
    CHEAPER_FIRST(R.string.cheaper_first),
    EXPENSIVE_FIRST(R.string.expensive_first),
    EARLY_ARRIVAL(R.string.early_arrival),
    LATE_ARRIVAL(R.string.late_arrival),
    EARLY_DEPARTURE(R.string.early_departure),
    LATE_DEPARTURE(R.string.late_departure),
    TRAVEL_DURATION(R.string.travel_duration)
}