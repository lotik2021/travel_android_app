package ru.movista.data.source.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ObjectTripType(val type: String) : Parcelable {
    FLIGHT("tripFlight"),
    TRAIN("tripTrain"),
    TRAIN_SUBURBAN("tripTrainSuburban"),
    BUS("tripBus"),
    FOOT("tripFoot"),
    TAXI("tripTaxi"),
    TRIP_BUS_TRANSFER("tripBusTransfer"),
    TRIP_TRAIN_SUBURBAN_TRANSFER("tripTrainSuburbanTransfer"),
    OTHER("other");

    companion object {
        fun getById(id: String): ObjectTripType {
            values().forEach {
                if (it.type == id) {
                    return it
                }
            }
            return OTHER
        }
    }
}

fun ObjectTripType?.isTransfer() : Boolean {
    return this in arrayListOf(
        ObjectTripType.FOOT,
        ObjectTripType.TAXI,
        ObjectTripType.TRIP_BUS_TRANSFER,
        ObjectTripType.TRIP_TRAIN_SUBURBAN_TRANSFER
    )
}

fun ObjectTripType?.isNotTransfer() : Boolean {
    return this in arrayListOf(
        ObjectTripType.FLIGHT,
        ObjectTripType.TRAIN,
        ObjectTripType.TRAIN_SUBURBAN,
        ObjectTripType.BUS
    )
}

fun ObjectTripType.toTripType(): TripType {
    return when (this) {
        ObjectTripType.FLIGHT -> TripType.FLIGHT
        ObjectTripType.TRAIN -> TripType.TRAIN
        ObjectTripType.TRAIN_SUBURBAN -> TripType.TRAIN_SUBURBAN
        ObjectTripType.TRIP_TRAIN_SUBURBAN_TRANSFER -> TripType.TRAIN_SUBURBAN
        ObjectTripType.BUS -> TripType.BUS
        ObjectTripType.TRIP_BUS_TRANSFER -> TripType.BUS
        ObjectTripType.TAXI -> TripType.TAXI
        else -> TripType.OTHER
    }
}