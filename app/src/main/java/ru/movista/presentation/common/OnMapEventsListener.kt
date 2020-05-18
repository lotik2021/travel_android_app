package ru.movista.presentation.common

interface OnMapEventsListener {
    fun onMapReady()

    fun onMapZoomClicked() {}
    fun onMapZoomInClicked() {}
    fun onMapZoomOutClicked() {}

    fun onMapTrafficEnabled() {}

    fun onMapTrafficDisabled() {}

    fun onMapCurrentLocationClicked() {}

    fun onMapOrientationClicked() {}
}