package ru.movista.data.source.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ObjectServiceType(val type: String) : Parcelable {
    BUS_BY_PARTNER("serviceBusByPartnerSearch"),
    BUS_OWN("serviceBusOwnSearch"),
    NOT_FOR_SALE("serviceNotForSaleSearch"),
    TRAIN_OWN("serviceTrainOwnSearch"),
    FLIGHT_FARE_FAM_OWN("serviceFlightFareFamOwnSearch"),
    FLIGHT_OWN("serviceFlightOwnSearch"),
    TRAIN_BY_PARTNER("serviceTrainByPartnerSearch"),
    TRAIN_UFS("serviceTrainUfsSearch"),
    OTHER("other");

    companion object {
        fun getById(type: String): ObjectServiceType {
            values().forEach {
                if (it.type == type) {
                    return it
                }
            }
            return OTHER
        }
    }
}

fun ObjectServiceType?.byPartner(): Boolean {
    return this in arrayListOf(
        ObjectServiceType.BUS_BY_PARTNER,
        ObjectServiceType.TRAIN_BY_PARTNER
    )
}

fun ObjectServiceType?.isTrain(): Boolean {
    return this in arrayListOf(
        ObjectServiceType.TRAIN_OWN,
        ObjectServiceType.TRAIN_BY_PARTNER,
        ObjectServiceType.TRAIN_UFS
    )
}

fun ObjectServiceType?.isFlight(): Boolean {
    return this in arrayListOf(
        ObjectServiceType.FLIGHT_FARE_FAM_OWN,
        ObjectServiceType.FLIGHT_OWN
    )
}

fun ObjectServiceType?.isOwnPurchase(): Boolean {
    return this in arrayListOf(
        ObjectServiceType.BUS_OWN,
        ObjectServiceType.TRAIN_OWN,
        ObjectServiceType.FLIGHT_OWN,
        ObjectServiceType.FLIGHT_FARE_FAM_OWN
    )
}