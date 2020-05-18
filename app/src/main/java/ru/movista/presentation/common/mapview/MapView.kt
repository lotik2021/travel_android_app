package ru.movista.presentation.common.mapview

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface MapView : MvpView {

    fun changeCurrentLocationPosition(lat: Double, lon: Double)

    fun changeCurrentLocationOrientation(bias: Float, rotateMap: Boolean = false)

    fun hideCurrentLocation()

    fun hideOrientation()

    fun requestLocationPermission()

    fun moveMap(
        lat: Double,
        lon: Double,
        withAnimation: Boolean,
        withMaxZoom: Boolean
    )

    fun stopMapRotating()

    fun drawAccuracy(latitude: Double, longitude: Double, radius: Double)

    fun zoomIn()
    fun zoomOut()

    fun showTraffic()
    fun hideTraffic()
    fun changeOrientationIcon(enabled: Boolean)


}