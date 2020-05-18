package ru.movista.presentation.common

interface OnLocationPermissionResultListener {
    fun onLocationPermissionReceived(lat: Double, lon: Double)
    fun onLastLocationPermissionDenied() {}
    fun onLastLocationPermissionNeverAskAgain() {}

    fun onNeverAskLocationClicked() {}
    fun onNeverAskLocationLaterClicked() {}
}