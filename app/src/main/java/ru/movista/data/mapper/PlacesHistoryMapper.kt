package ru.movista.data.mapper

import ru.movista.data.entity.FavouritePlacesResponse
import ru.movista.data.entity.RecentPlacesResponse
import ru.movista.di.FragmentScope
import ru.movista.domain.model.FavouritePlace
import ru.movista.domain.model.RecentPlace
import javax.inject.Inject

@FragmentScope
class PlacesHistoryMapper @Inject constructor() {

    fun toFavouritePlaces(favouritePlacesRequest: FavouritePlacesResponse): List<FavouritePlace> {
        val result = arrayListOf<FavouritePlace>()
        favouritePlacesRequest.result.forEach {
            result.add(
                FavouritePlace(
                    it.main_text,
                    it.secondary_text,
                    it.location.latitude,
                    it.location.longitude,
                    it.place_id,
                    it.type,
                    it.android_icon_url,
                    it.ios_icon_url_dark
                )
            )
        }
        return result
    }

    fun toRecentPlaces(recentPlacesResponse: RecentPlacesResponse): List<RecentPlace> {
        val result = arrayListOf<RecentPlace>()
        recentPlacesResponse.result.forEach {
            result.add(
                RecentPlace(
                    it.main_text,
                    it.secondary_text,
                    it.location.latitude,
                    it.location.longitude,
                    it.place_id,
                    it.types,
                    it.android_icon_url,
                    it.ios_icon_url_dark
                )
            )
        }
        return result
    }
}