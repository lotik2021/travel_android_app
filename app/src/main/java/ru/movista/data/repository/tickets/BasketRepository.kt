package ru.movista.data.repository.tickets

import io.reactivex.Single
import ru.movista.data.entity.tickets.*
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class BasketRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage
) {
    companion object {
        private const val SHOW_BASKET_HINT = "SHOW_BASKET_HINT"
    }

    fun getPathGroup(request: PathGroupRequest): Single<PathGroupResponse> {
        return api.getPathGroup(keyValueStorage.getToken(), request)
    }

    fun getSegmentRoutes(request: SegmentRoutesRequest): Single<SegmentRoutesResponse> {
        return api.getSegmentRoutes(keyValueStorage.getToken(), request)
    }

    fun saveSelectedRoutes(request: SaveSelectedRoutesRequest): Single<SaveSelectedRoutesResponse> {
        return api.saveSelectedRoutes(keyValueStorage.getToken(), request)
    }

    fun isShowBasketHint(): Boolean {
        return keyValueStorage.getBoolean(SHOW_BASKET_HINT, true)
    }

    fun saveBasketHintVisibility(visibility: Boolean) {
        keyValueStorage.putBoolean(SHOW_BASKET_HINT, visibility)
    }
}