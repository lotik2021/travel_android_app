package ru.movista.presentation.viewmodel

sealed class ExtrasViewModel

data class ToLocationExtrasViewModel(
    val lat: Double,
    val lon: Double
) : ExtrasViewModel()

object UndefienedExtrasViewModel : ExtrasViewModel()