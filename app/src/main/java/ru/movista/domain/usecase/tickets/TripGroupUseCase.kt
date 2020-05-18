package ru.movista.domain.usecase.tickets

import io.reactivex.Observable
import ru.movista.data.entity.ApiError
import ru.movista.data.entity.tickets.AsyncSearchRequest
import ru.movista.data.entity.tickets.SearchStatusRequest
import ru.movista.data.entity.tickets.common.Customer
import ru.movista.data.entity.tickets.common.Error
import ru.movista.data.entity.tickets.common.SearchParams
import ru.movista.data.mapper.tickets.TripGroupMapper
import ru.movista.data.repository.tickets.TripGroupRepository
import ru.movista.data.source.local.models.TripType
import ru.movista.di.FragmentScope
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.model.tickets.TripGroupData
import ru.movista.utils.EMPTY
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class TripGroupUseCase @Inject constructor(
    private val repository: TripGroupRepository,
    private val ticketSearchMapper: TripGroupMapper
) {
    private var currentSyncSearchDate = String.EMPTY
    private var isCompletedSearch = false

    fun asyncTicketsSearch(params: SearchModel): Observable<Pair<TripGroupData, Boolean>> {
        return repository.asyncTicketsSearch(createAsyncSearchRequest(params))
            .doOnSuccess { result -> throwIfError(result.errors) }
            .flatMapObservable { asyncResult ->
                currentSyncSearchDate = asyncResult.data.dateTimeUpdate

                if (asyncResult.data.isComplete) {
                    repository
                        .getSearchResults(SearchStatusRequest(asyncResult.data.uid))
                        .map { ticketSearchMapper.toTicketSearchResult(asyncResult.data.uid, it) to true }
                } else {
                    Observable.timer(1, TimeUnit.SECONDS)
                        .flatMap {
                            repository.getSearchStatus(SearchStatusRequest(asyncResult.data.uid))
                        }
                        .doOnNext { result -> throwIfError(result.errors)}
                        .repeat()
                        .takeUntil { status ->
                            currentSyncSearchDate = status.data.dateTimeUpdate
                            isCompletedSearch = status.data.isComplete
                            isCompletedSearch
                        }
                        .filter { status ->
                            isCompletedSearch = status.data.isComplete
                            currentSyncSearchDate.isNotEmpty() && currentSyncSearchDate != status.data.dateTimeUpdate
                        }
                        .flatMap { status ->
                            repository.getSearchResults(SearchStatusRequest(status.data.uid))
                        }
                        .doOnNext { result -> throwIfError(result.errors)}
                        .map {
                            ticketSearchMapper.toTicketSearchResult(asyncResult.data.uid, it) to isCompletedSearch
                        }
                }
            }
    }

    fun isShowSearchHint(): Boolean {
        return repository.isShowSearchHint()
    }

    fun saveDisablingSearchHint() {
        repository.saveSearchHintVisibility(false)
    }

    private fun createAsyncSearchRequest(params: SearchModel): AsyncSearchRequest {
        val customersRequest = mutableListOf<Customer>()

        params.passengers.forEach { passenger ->
            customersRequest.add(Customer(passenger.age, passenger.isSeatRequired))
        }

        return AsyncSearchRequest(
            SearchParams().apply {
                currencyCode = "RUB"
                cultureCode = "RU"
                departureBegin = params.departureDate.toString()
                returnDepartureBegin = params.arrivalDate?.toString()
                from = params.fromPlace?.id ?: 0
                to = params.toPlace?.id ?: 0
                tripTypes = TripType.getBasicTripTypes().map { it.id }
                airServiceClass = params.comfortType.value
                customers = customersRequest
            }
        )
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