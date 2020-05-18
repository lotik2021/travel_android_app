package ru.movista.data.source.local.models

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize
import ru.movista.R

@Parcelize
enum class RouteFilterType(@StringRes val titleResId: Int) : Parcelable {
    DEPARTURE_POINTS(R.string.departure_points),
    ARRIVAL_POINTS(R.string.arrival_points),
    CARRIERS(R.string.carriers)
}