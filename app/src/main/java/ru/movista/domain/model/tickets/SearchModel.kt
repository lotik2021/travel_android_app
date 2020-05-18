package ru.movista.domain.model.tickets

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class SearchModel(
    var fromPlace: Place? = null,
    var toPlace: Place? = null,
    var departureDate: LocalDate? = null,
    var arrivalDate: LocalDate? = null,
    var passengers: ArrayList<Passenger> = arrayListOf(),
    var comfortType: ComfortType = ComfortType.ECONOMY
) : Parcelable