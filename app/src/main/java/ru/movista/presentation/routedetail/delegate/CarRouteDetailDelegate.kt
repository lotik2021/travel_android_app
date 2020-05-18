package ru.movista.presentation.routedetail.delegate

import androidx.core.content.res.ResourcesCompat
import ru.movista.R
import ru.movista.presentation.routedetail.RouteDetailPresenter
import ru.movista.presentation.viewmodel.CarRouteViewModel

class CarRouteDetailDelegate(
    private val routeDetailPresenter: RouteDetailPresenter,
    private val carRouteViewModel: CarRouteViewModel
) : RouteDetailTypeDelegate {
    private val viewState = routeDetailPresenter.viewState
    private val resources = routeDetailPresenter.resources

    init {
        viewState.showCarRouteDetails(carRouteViewModel)
    }

    override fun onMapReady() {
        viewState.showTraffic()
        viewState.showRouteOnMap(
            polyline = carRouteViewModel.carPolyline,
            color = ResourcesCompat.getColor(resources, R.color.trip_car, null),
            solidLine = false,
            showAllRoute = true
        )
    }

    override fun onTrafficEnabled() {
        viewState.clearDrawnRoute()
        viewState.showRouteOnMap(
            polyline = carRouteViewModel.carPolyline,
            color = ResourcesCompat.getColor(resources, R.color.trip_car, null),
            solidLine = false,
            showAllRoute = false
        )
    }

    override fun onTrafficDisabled() {
        viewState.clearDrawnRoute()
        viewState.showRouteOnMap(
            polyline = carRouteViewModel.carPolyline,
            color = ResourcesCompat.getColor(resources, R.color.trip_car, null),
            solidLine = true,
            showAllRoute = false
        )
    }
}