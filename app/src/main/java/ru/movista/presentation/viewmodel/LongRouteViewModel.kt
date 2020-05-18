package ru.movista.presentation.viewmodel

import ru.movista.R
import java.io.Serializable

data class MovistaRouteViewModel(
    val id: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val duration: String,
    val changesCount: String,
    val price: String,
    val redirectUrl: String,
    val trips: List<LongRouteTripType>
) : Serializable

sealed class LongRouteTripType(
    val icon: Int
)

data class FlightLongRouteTripType(
    val flightIcon: Int = R.drawable.movista_flight
) : LongRouteTripType(flightIcon)

data class BusLongRouteTripType(
    val busIcon: Int = R.drawable.movista_bus
) : LongRouteTripType(busIcon)

data class TrainLongRouteTripType(
    val trainIcon: Int = R.drawable.movista_train
) : LongRouteTripType(trainIcon)