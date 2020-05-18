package ru.movista.data.repository.tickets

import io.reactivex.Observable
import io.reactivex.Single
import ru.movista.data.entity.tickets.AsyncSearchRequest
import ru.movista.data.entity.tickets.AsyncSearchResponse
import ru.movista.data.entity.tickets.SearchResultsResponse
import ru.movista.data.entity.tickets.SearchStatusRequest
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class TripGroupRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage
) {
    companion object {
        private const val SHOW_SEARCH_HINT = "SHOW_SEARCH_HINT"
    }

    fun asyncTicketsSearch(asyncSearchRequest: AsyncSearchRequest): Single<AsyncSearchResponse> {
        return api.asyncTicketsSearch(keyValueStorage.getToken(), asyncSearchRequest)
    }

    fun getSearchStatus(request: SearchStatusRequest): Observable<AsyncSearchResponse> {
        return api.getSearchStatus(keyValueStorage.getToken(), request)
    }

    fun getSearchResults(request: SearchStatusRequest): Observable<SearchResultsResponse> {
        return api.getSearchResults(keyValueStorage.getToken(), request)
    }

    fun isShowSearchHint(): Boolean {
        return keyValueStorage.getBoolean(SHOW_SEARCH_HINT, true)
    }

    fun saveSearchHintVisibility(visibility: Boolean) {
        keyValueStorage.putBoolean(SHOW_SEARCH_HINT, visibility)
    }
}