package ru.movista.presentation.placesearch

import ru.movista.presentation.viewmodel.UserPlacesViewModel

interface UserHistoryView {
    fun showUserHistory(historyItems: List<UserPlacesViewModel>, listener: (Int) -> Unit)
}