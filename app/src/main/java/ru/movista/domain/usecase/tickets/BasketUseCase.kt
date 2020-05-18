package ru.movista.domain.usecase.tickets

import io.reactivex.Single
import ru.movista.data.entity.ApiError
import ru.movista.data.entity.tickets.PathGroupRequest
import ru.movista.data.entity.tickets.SaveSelectedRoutesData
import ru.movista.data.entity.tickets.SaveSelectedRoutesRequest
import ru.movista.data.entity.tickets.SegmentRoutesRequest
import ru.movista.data.entity.tickets.common.Error
import ru.movista.data.mapper.tickets.PathGroupMapper
import ru.movista.data.repository.tickets.BasketRepository
import ru.movista.data.source.local.DEFAULT_UTM_SOURCE
import ru.movista.data.source.local.TICKETS_PAY_CART_URL_PATTERN
import ru.movista.data.source.local.models.isOwnPurchase
import ru.movista.di.FragmentScope
import ru.movista.domain.model.tickets.PathGroup
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.Segment
import ru.movista.utils.EMPTY
import javax.inject.Inject

@FragmentScope
class BasketUseCase @Inject constructor(
    private val repository: BasketRepository,
    private val mapper: PathGroupMapper
) {
    fun getPathGroup(uid: String, pathGroupId: String): Single<PathGroup> {
        return repository.getPathGroup(PathGroupRequest(uid, pathGroupId))
            .doOnSuccess { result -> throwIfError(result.errors) }
            .map { mapper.map(it) }
    }

    fun getSegmentRoutes(
        uid: String,
        selectedRouteIds: List<String>,
        groupId: String,
        segmentId: String
    ): Single<List<Route>> {
        val request = SegmentRoutesRequest(
            uid = uid,
            selectedRouteIds = selectedRouteIds,
            groupId = groupId,
            segmentId = segmentId
        )
        return repository.getSegmentRoutes(request)
            .doOnSuccess { result -> throwIfError(result.errors) }
            .map { mapper.segmentRoutesMap(it) }
    }

    fun saveSelectedRoutes(
        uid: String,
        groupId: String,
        segments: List<Segment>
    ): Single<SaveSelectedRoutesData> {
        val selectedRouteIds = segments.mapNotNull { it.selectedRoute?.id }
        val isOwnPurchase = segments.any { segment ->
            segment.selectedRoute?.tripsToServices?.any {
                it.service.objectType.isOwnPurchase()
            } ?: false
        }

        val request = SaveSelectedRoutesRequest(
            uid = uid,
            selectedRouteIds = selectedRouteIds,
            groupId = groupId
        )

        return repository.saveSelectedRoutes(request)
            .doOnSuccess { result -> throwIfError(result.errors) }
            .doOnSuccess { response ->
                val data = response.data
                if (data != null) {
                    if (isOwnPurchase) {
                        data.bookingUrl?.let { url ->
                            val buyUrl = "$url&$DEFAULT_UTM_SOURCE"
                            data.actualUrl = buyUrl
                        }
                    } else {
                        data.uid?.let { uid ->
                            val buyUrl = String.format(
                                TICKETS_PAY_CART_URL_PATTERN,
                                uid,
                                DEFAULT_UTM_SOURCE
                            )

                            data.actualUrl = buyUrl
                        }
                    }
                }
            }
            .map { it.data }
    }

    fun isShowBasketHint(): Boolean {
        return repository.isShowBasketHint()
    }

    fun saveDisablingBasketHint() {
        repository.saveBasketHintVisibility(false)
    }

    private fun throwIfError(apiError: List<Error>?) {
        if (apiError.isNullOrEmpty()) {
            return
        }

        val errorList = apiError.filter { it.isError == true }
        if (errorList.isNotEmpty()) {
            throw ApiError(
                apiError.first().code ?: String.EMPTY,
                apiError.first().message ?: String.EMPTY
            )
        }
    }
}