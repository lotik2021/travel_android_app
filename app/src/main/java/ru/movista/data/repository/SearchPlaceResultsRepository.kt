package ru.movista.data.repository

import io.reactivex.Single
import ru.movista.data.entity.FavouritePlacesRequest
import ru.movista.data.entity.RecentPlacesRequest
import ru.movista.data.mapper.PlacesHistoryMapper
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import ru.movista.domain.model.FavouritePlace
import ru.movista.domain.model.RecentPlace
import javax.inject.Inject

@FragmentScope
class SearchPlaceResultsRepository @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val deviceInfo: DeviceInfo,
    private val api: Api,
    private val placesHistoryMapper: PlacesHistoryMapper
) {
    fun getRecents(count: Int): Single<List<RecentPlace>> {
        return api.getRecentPlaces(
            keyValueStorage.getToken(),
            RecentPlacesRequest(deviceInfo.deviceId, count)
        ).map {
            placesHistoryMapper.toRecentPlaces(it)
        }
    }

    fun getFavourites(count: Int): Single<List<FavouritePlace>> {
        return api.getFavouritePlaces(
            keyValueStorage.getToken(),
            FavouritePlacesRequest(deviceInfo.deviceId, count)
        ).map {
            placesHistoryMapper.toFavouritePlaces(it)
        }
    }
}