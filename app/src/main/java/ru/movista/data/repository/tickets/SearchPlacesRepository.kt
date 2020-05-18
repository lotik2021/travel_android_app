package ru.movista.data.repository.tickets

import io.reactivex.Single
import ru.movista.data.entity.tickets.SearchPlacesRequest
import ru.movista.data.mapper.tickets.PlacesMapper
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FlowScope
import ru.movista.domain.model.tickets.Place
import javax.inject.Inject

@FlowScope
class SearchPlacesRepository @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val api: Api,
    private val deviceInfo: DeviceInfo,
    private val placesMapper: PlacesMapper
) {
    fun searchPlaces(query: String, resultCount: Int? = null): Single<List<Place>> {
        return api.searchPlacesByName(
            keyValueStorage.getToken(),
            if (resultCount != null) SearchPlacesRequest(query, resultCount) else SearchPlacesRequest(query)
        ).map { placesMapper.toPlaces(it.result) }
    }
}