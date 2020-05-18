package ru.movista.presentation.viewmodel

import java.io.Serializable

data class RouteViewModel(
    val id: String,
    val departureName: String,
    val destinationName: String,
    val endTime: String,
    val durationMinutes: String,
    val durationWithHours: String,
    val startToEndTime: String = "20:07 — 21:02",
    val minPrice: String = "",
    val trips: List<TripViewModel>,
    val agencies: List<AgencyViewModel>
) : Serializable

sealed class RouteDetailsViewModel : Serializable

data class GoogleRouteDetailsViewModel(
    val id: String,
    val departureName: String,
    val destinationName: String,
    val endTime: String,
    val durationMinutes: String,
    val durationWithHours: String,
    val startToEndTime: String = "20:07 — 21:02",
    val minPrice: String = "",
    val trips: List<TripDetailsViewModel>,
    val agencies: List<AgencyViewModel>
) : RouteDetailsViewModel(), Serializable

sealed class CarRouteViewModel(
    val carId: String,
    val carMinDuration: String,
    val carMaxDuration: String,
    val carDistance: String,
    val carStartTime: String,
    val carMinEndTime: String,
    val carMaxEndTime: String,
    val carDeeplinks: List<CarDeeplinkViewModel>,
    val carRouteThrough: String,
    val carPolyline: String
) : RouteDetailsViewModel(), Serializable

data class TaxiProviderViewModel(
    val taxiProviderId: String,
    val taxiProviderStartTime: String,
    val taxiProviderEndTime: String,
    val taxiProviderDuration: String,
    val taxiProviderMinPrice: String,
    val taxiProviderDescription: String,
    val taxiProviderLink: String,
    val taxiProviderStoreLink: String,
    val taxiProviderDeeplinkId: String,
    val taxiProviderTariffs: List<TaxiTariff>,
    val taxiProviderImage: String,
    val taxiProviderIosImage: String,
    val taxiProviderPolyline: String
) : RouteDetailsViewModel(), Serializable

data class GoogleCarViewModel(
    val id: String,
    val minDuration: String,
    val maxDuration: String,
    val distance: String,
    val startTime: String,
    val minEndTime: String,
    val maxEndTime: String,
    val deeplinks: List<CarDeeplinkViewModel>,
    val routeThrough: String,
    val polyline: String

) : CarRouteViewModel(
    id,
    minDuration,
    maxDuration,
    distance,
    startTime,
    minEndTime,
    maxEndTime,
    deeplinks,
    routeThrough,
    polyline
), Serializable

data class CarDeeplinkViewModel(
    val carDeepLinkTitle: String,
    val carDeepLinkUrl: String,
    val carDeepLinkImage: String
) : Serializable


data class TaxiTariff(
    val taxiTariffName: String,
    val taxiTariffPrice: String
) : Serializable



