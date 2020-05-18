package ru.movista.presentation.tickets.segments

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import ru.movista.data.source.local.models.ObjectTripType

@Parcelize
data class RouteFilterData(
    val maxTripDuration: Long,
    val minTripDuration: Long,
    val maxPrice: Int,
    val minPrice: Int,
    val maxTransferCount: Int,
    val carriers: List<RoutePlaceFilter>,
    val departurePoints: List<RoutePlaceFilter>,
    val arrivalPoints: List<RoutePlaceFilter>
) : Parcelable

@Parcelize
data class UserRouteFilter(
    var departureMinTime: LocalTime? = null,
    var departureMaxTime: LocalTime? = null,

    var arrivalMinTime: LocalTime? = null,
    var arrivalMaxTime: LocalTime? = null,

    var maxTripDuration: Long = 0,

    var minPrice: Int = 0,
    var maxPrice: Int = 0,

    var maxTransferCount: Int = 0,

    var carriers: List<RoutePlaceFilter>,
    var departurePoints: List<RoutePlaceFilter>,
    var arrivalPoints: List<RoutePlaceFilter>
) : Parcelable {

    fun departureMinTimeMinutes(): Long {
        return ChronoUnit.MINUTES.between(LocalTime.of(0, 0), departureMinTime)
    }

    fun departureMaxTimeMinutes(): Long {
        return ChronoUnit.MINUTES.between(LocalTime.of(0, 0), departureMaxTime)
    }

    fun arrivalMinTimeMinutes(): Long {
        return ChronoUnit.MINUTES.between(LocalTime.of(0, 0), arrivalMinTime)
    }

    fun arrivalMaxTimeMinutes(): Long {
        return ChronoUnit.MINUTES.between(LocalTime.of(0, 0), arrivalMaxTime)
    }

    fun resetCarriers() {
        carriers.forEach { item -> item.routeItems.forEach { it.isSelected = true } }
    }

    fun resetDeparturePoints() {
        departurePoints.forEach { item -> item.routeItems.forEach { it.isSelected = true } }
    }

    fun resetArrivalPoints() {
        arrivalPoints.forEach { item -> item.routeItems.forEach { it.isSelected = true } }
    }


    fun totalCarriersSize(): Int = carriers.sumBy { it.routeItems.size }

    fun selectedCarriersSize(): Int {
        return carriers.sumBy { route -> route.routeItems.filter { it.isSelected }.count() }
    }


    fun totalDeparturePointsSize(): Int = departurePoints.sumBy { it.routeItems.size }

    fun selectedDeparturePointsSize(): Int {
        return departurePoints.sumBy { route -> route.routeItems.filter { it.isSelected }.count() }
    }


    fun totalArrivalPointsSize(): Int = arrivalPoints.sumBy { it.routeItems.size }

    fun selectedArrivalPointsSize(): Int {
        return arrivalPoints.sumBy { route -> route.routeItems.filter { it.isSelected }.count() }
    }


    fun selectedCarriersNames(): List<String> {
        return carriers
            .map { route -> route.routeItems.filter { it.isSelected }.map { it.name } }
            .flatten()
    }

    fun selectedDeparturePointsNames(): List<String> {
        return departurePoints
            .map { route -> route.routeItems.filter { it.isSelected }.map { it.name } }
            .flatten()
    }

    fun selectedArrivalPointsNames(): List<String> {
        return arrivalPoints
            .map { route -> route.routeItems.filter { it.isSelected }.map { it.name } }
            .flatten()
    }
}

@Parcelize
data class RoutePlaceFilter(
    val title: String,
    val tripType: ObjectTripType,
    val routeItems: List<RouteItemPlaceFilter>
) : Parcelable {

    fun copy(): RoutePlaceFilter {
        return RoutePlaceFilter(title, tripType, routeItems.map { it.copy() })
    }
}

@Parcelize
data class RouteItemPlaceFilter(
    val name: String,
    var isSelected: Boolean = true
) : Parcelable