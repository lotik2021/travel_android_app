package ru.movista.presentation.converter

import ru.movista.data.entity.SearchPlaceResponse
import ru.movista.data.entity.objects.AddressesDataEntity
import ru.movista.di.FragmentScope
import ru.movista.domain.model.FavouritePlace
import ru.movista.domain.model.PlaceSearchHistoryItem
import ru.movista.domain.model.RecentPlace
import ru.movista.presentation.viewmodel.ChatAddressViewModel
import ru.movista.presentation.viewmodel.SearchPlaceViewModel
import ru.movista.presentation.viewmodel.UserPlacesViewModel
import javax.inject.Inject

@FragmentScope
class PlaceSearchResultConverter @Inject constructor() {

    fun toChatAddressesViewModel(data: AddressesDataEntity): List<ChatAddressViewModel> {
        val result = arrayListOf<ChatAddressViewModel>()
        data.addresses.forEach { searchPlaceResponseItem ->
            result.add(
                ChatAddressViewModel(
                    searchPlaceResponseItem.main_text,
                    searchPlaceResponseItem.secondary_text,
                    searchPlaceResponseItem.place_id,
                    searchPlaceResponseItem.android_icon_url,
                    searchPlaceResponseItem.ios_icon_url_dark
                )
            )
        }
        return result
    }

    fun toUserPlacesViewModel(placeSearchHistoryItems: List<PlaceSearchHistoryItem>): List<UserPlacesViewModel> {
        val result = arrayListOf<UserPlacesViewModel>()

        placeSearchHistoryItems.forEach { placeSearchHistoryItem ->
            if (placeSearchHistoryItem is RecentPlace) {
                result.add(
                    UserPlacesViewModel(
                        placeSearchHistoryItem.name,
                        placeSearchHistoryItem.description,
                        placeSearchHistoryItem.icon,
                        placeSearchHistoryItem.iosIcon
                    )
                )
            } else if (placeSearchHistoryItem is FavouritePlace) {
                result.add(
                    UserPlacesViewModel(
                        placeSearchHistoryItem.name,
                        placeSearchHistoryItem.description,
                        placeSearchHistoryItem.icon,
                        placeSearchHistoryItem.iosIcon
                    )
                )
            }
        }
        return result
    }

    fun toSearchPlaceViewModel(searchPlaceResponse: SearchPlaceResponse): List<SearchPlaceViewModel> {
        val result: ArrayList<SearchPlaceViewModel> = arrayListOf()

        if (searchPlaceResponse.result.isEmpty()) return emptyList()

        searchPlaceResponse.result.forEach { searchPlaceResponseItem ->
            result.add(
                SearchPlaceViewModel(
                    searchPlaceResponseItem.main_text,
                    searchPlaceResponseItem.secondary_text,
                    searchPlaceResponseItem.android_icon_url,
                    searchPlaceResponseItem.ios_icon_url_dark
                )
            )
        }

        return result
    }
}