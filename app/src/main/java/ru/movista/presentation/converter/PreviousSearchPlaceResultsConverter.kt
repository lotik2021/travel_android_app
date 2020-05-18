package ru.movista.presentation.converter

import android.content.res.Resources
import ru.movista.R
import ru.movista.di.FragmentScope
import ru.movista.domain.model.FavouritePlace
import ru.movista.domain.model.PlaceSearchHistoryItem
import ru.movista.presentation.viewmodel.CategorySearchPlaceViewModel
import ru.movista.presentation.viewmodel.ItemSearchPlaceViewModel
import ru.movista.presentation.viewmodel.UserSearchPlaceResultsViewModel
import javax.inject.Inject

@FragmentScope
class PreviousSearchPlaceResultsConverter @Inject constructor(private val resources: Resources) {

    fun toUserSearchResultsViewModel(items: List<PlaceSearchHistoryItem>): List<UserSearchPlaceResultsViewModel> {
        val result = arrayListOf<UserSearchPlaceResultsViewModel>()

        // допускаем, что пришел отсортированный (избранное-недавнее) список
        val favCount = items.filter { it is FavouritePlace }.size
        items.forEachIndexed { index, placeSearchHistoryItem ->
            when (index) {
                0 -> {
                    result.add(CategorySearchPlaceViewModel(resources.getString(R.string.favourites)))
                }
                favCount -> {
                    result.add(CategorySearchPlaceViewModel(resources.getString(R.string.recents)))
                }
                else -> {
                }
            }
            result.add(
                ItemSearchPlaceViewModel(
                    placeSearchHistoryItem.placeName,
                    placeSearchHistoryItem.placeDescription
                )
            )
        }
        return result
    }
}