package ru.movista.presentation.routedetail

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.data.repository.TaxiOrderRepository
import ru.movista.presentation.refilltravelcard.TravelCardViewModel
import ru.movista.presentation.viewmodel.CarRouteViewModel
import ru.movista.presentation.viewmodel.GoogleRouteDetailsViewModel
import ru.movista.presentation.viewmodel.TaxiProviderViewModel
import ru.movista.presentation.viewmodel.TripViewModel

interface RouteDetailView : MvpView {

    fun showTripsOnMap(
        trips: List<TripViewModel>,
        solidLine: Boolean = true,
        showAllRoute: Boolean = true
    )

    fun clearDrawnRoute()

    fun showRouteOnMap(
        polyline: String,
        color: Int,
        solidLine: Boolean = true,
        showAllRoute: Boolean = true
    )

    fun showGoogleRouteDetail(
        routeViewModel: GoogleRouteDetailsViewModel,
        baseTrips: List<TripViewModel>,
        showFooterInfo: Boolean
    )

    @StateStrategyType(SkipStrategy::class)
    fun showTaxiOrderDialog(taxiProviderInfo: TaxiProviderViewModel)

    @StateStrategyType(SkipStrategy::class)
    fun collapseBottomDialog()

    fun showRefillTravelCardFabLoading()
    fun hideRefillTravelCardFabLoading()

    @StateStrategyType(SkipStrategy::class)
    fun showRefillTravelCardDialog(travelCardViewModel: ArrayList<TravelCardViewModel>)

    @StateStrategyType(SkipStrategy::class)
    fun zoomInTrip(tripPolyline: String)

    fun showCarRouteDetails(carRouteViewModel: CarRouteViewModel)
    fun showTaxiOrderDetails(
        taxiProviderViewModel: TaxiProviderViewModel,
        taxiOrderRepository: TaxiOrderRepository
    )

    fun showTraffic()
}