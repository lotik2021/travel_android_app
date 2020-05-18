package ru.movista.presentation.routedetail.delegate

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import ru.movista.R
import ru.movista.presentation.routedetail.RouteDetailPresenter
import ru.movista.presentation.viewmodel.TaxiProviderViewModel

class TaxiRouteDetailDelegate(
    private val routeDetailPresenter: RouteDetailPresenter,
    private val taxiProviderViewModel: TaxiProviderViewModel,
    private val resources: Resources
) : RouteDetailTypeDelegate {
    private val viewState = routeDetailPresenter.viewState

    init {
        viewState.showTaxiOrderDetails(
            taxiProviderViewModel,
            routeDetailPresenter.taxiOrderRepository
        )
    }

    override fun onMapReady() {
        viewState.showRouteOnMap(
            taxiProviderViewModel.taxiProviderPolyline,
            ResourcesCompat.getColor(resources, R.color.trip_taxi, null)
        )
    }

    override fun onTrafficEnabled() {
        viewState.clearDrawnRoute()
        viewState.showRouteOnMap(
            polyline = taxiProviderViewModel.taxiProviderPolyline,
            color = ResourcesCompat.getColor(resources, R.color.trip_taxi, null),
            solidLine = false,
            showAllRoute = false
        )
    }

    override fun onTrafficDisabled() {
        viewState.clearDrawnRoute()
        viewState.showRouteOnMap(
            polyline = taxiProviderViewModel.taxiProviderPolyline,
            color = ResourcesCompat.getColor(resources, R.color.trip_taxi, null),
            solidLine = true,
            showAllRoute = false
        )
    }
}