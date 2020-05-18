package ru.movista.presentation.viewmodel

sealed class UserSearchPlaceResultsViewModel

data class CategorySearchPlaceViewModel(
    val title: String
) : UserSearchPlaceResultsViewModel()

data class ItemSearchPlaceViewModel(
    val title: String,
    val text: String
) : UserSearchPlaceResultsViewModel()