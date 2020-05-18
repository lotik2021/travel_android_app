package ru.movista.presentation.common

interface OnAudioPermissionResultListener {
    fun onAudioPermissionGranted()
    fun onAudioPermissionDenied() {}
}