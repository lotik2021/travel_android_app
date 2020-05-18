package ru.movista.data.repository

import io.reactivex.Single
import ru.movista.data.entity.GetPlaceByIDDataResponse
import ru.movista.data.entity.GetPlaceByIDRequest
import ru.movista.data.entity.SearchPlaceRequest
import ru.movista.data.entity.SearchPlaceResponse
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import ru.movista.domain.model.Place
import javax.inject.Inject

@FragmentScope
class SearchPlaceRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage
) {

    var currentGoogleSessionToken: String = "1"

    fun searchPlace(query: String): Single<SearchPlaceResponse> {
        return api
            .search(
                keyValueStorage.getToken(),
                SearchPlaceRequest(currentGoogleSessionToken, input = query)
            )
            .doOnEvent { response, _ ->
                storeGoogleSessionToken(response)
                if (response.error != null) {
                    throw response.error
                }
            }
    }

    fun getPlaceByID(id: String): Single<Place> {
        return api.getPlaceByID(
            keyValueStorage.getToken(),
            id,
            GetPlaceByIDRequest(currentGoogleSessionToken)
        ).map {
            toPlace(it.result)
        }
    }

    private fun storeGoogleSessionToken(response: SearchPlaceResponse?) {
        currentGoogleSessionToken = response?.sessionToken ?: return
    }

    private fun toPlace(getPlaceByIDResponse: GetPlaceByIDDataResponse): Place {
        return with(getPlaceByIDResponse) {
            Place(lat = location.latitude, lon = location.longitude, placeId = place_id)
        }
    }


}
