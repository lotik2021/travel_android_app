package ru.movista.presentation.viewmodel

import ru.movista.R
import java.io.Serializable

/**
 * Включает в себя TripViewModel, DestinationDetailViewModel и TransitViewModel
 * для отображения списком деталей маршрута
 */
sealed class TripDetailsViewModel : Serializable

sealed class TripViewModel(
    val tripDuration: String,
    val tripPolyline: String,
    val tripStartTime: String,
    val tripEndTime: String,
    val tripIcon: String?,
    val iosTripIcon: String?,
    val tripColor: Int
) : TripDetailsViewModel(), Serializable

data class DestinationDetailViewModel(
    val addressName: String,
    val time: String
) : TripDetailsViewModel(), Serializable

data class TransitViewModel(
    val duration: String
) : TripDetailsViewModel()

data class OnFootViewModel(
    val distance: String,
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val color: Int = R.color.blue
) : TripViewModel(duration, polyline, startTime, endTime, null, null, color), Serializable

sealed class PublicTransportViewModel(
    publicTransportDuration: String,
    publicTransportPolyline: String,
    publicTransportStartTime: String,
    publicTransportEndTime: String,
    publicTransportIcon: String?,
    publicTransportIosIcon: String?,
    val publicTransportColor: Int,
    val publicTransportDetailIcon: String?,
    val publicTransportDetailIosIcon: String?,
    val publicTransportDepartureName: String,
    val publicTransportDestinationName: String,
    val publicTransportStropsCount: String,
    val publicTransportName: Int,
    val publicTransportLineNumber: String,
    val publicTransportLineName: String
) : TripViewModel(
    publicTransportDuration,
    publicTransportPolyline,
    publicTransportStartTime,
    publicTransportEndTime,
    publicTransportIcon,
    publicTransportIosIcon,
    publicTransportColor
), Serializable

data class BusViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val color: Int = R.color.trip_bus,
    val stopsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_bus,
    val lineNumber: String,
    val lineName: String
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    color,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stopsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class TramViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val color: Int = R.color.trip_tram,
    val stopsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_tram,
    val lineNumber: String,
    val lineName: String
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    color,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stopsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class HeavyRailViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val color: Int = R.color.trip_heavy_rail,
    val stopsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_heavy_rail,
    val lineNumber: String,
    val lineName: String
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    color,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stopsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class FerryViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val color: Int = R.color.trip_ferry,
    val stopsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_ferry,
    val lineNumber: String,
    val lineName: String
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    color,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stopsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class TrolleybusViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val color: Int = R.color.trip_trolleybus,
    val stopsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_trolleybus,
    val lineNumber: String,
    val lineName: String
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    color,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stopsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class ShareTaxiViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val color: Int = R.color.trip_share_taxi,
    val stopsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_share_taxi,
    val lineNumber: String,
    val lineName: String
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    color,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stopsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class SubwayViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val bgColor: String,
    val lineNumber: String,
    val lineName: String,
    val stationsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_subway
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    R.color.trip_on_foot,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stationsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class CommuterTrainViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val detailIcon: String?,
    val iosDetailIcon: String?,
    val color: Int = R.color.trip_commuter_train,
    val lineName: String,
    val stopsCount: String,
    val departureName: String,
    val destinationName: String,
    val name: Int = R.string.trip_by_commuter_train,
    val lineNumber: String,
    val tripShortName: String
) : PublicTransportViewModel(
    duration,
    polyline,
    startTime,
    endTime,
    icon,
    iosIcon,
    color,
    detailIcon,
    iosDetailIcon,
    departureName,
    destinationName,
    stopsCount,
    name,
    lineNumber,
    lineName
), Serializable

data class TaxiViewModel(
    val duration: String,
    val polyline: String,
    val startTime: String,
    val endTime: String,
    val icon: String?,
    val iosIcon: String?,
    val color: Int = R.color.trip_taxi,
    val fromAddress: String,
    val toAddress: String,
    val providers: List<TaxiProviderViewModel>
) : TripViewModel(duration, polyline, startTime, endTime, icon, iosIcon, color), Serializable


