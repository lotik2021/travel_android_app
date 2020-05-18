package ru.movista.domain.usecase.tickets.searchparams


import io.reactivex.Single
import ru.movista.domain.model.tickets.SearchModel

interface ISearchParamsRepository {

    fun setPassengersInfo()
    fun setTripTypes()
    fun getEmptySearchModel(): SearchModel
    fun search(chars: String): Single<Unit>
    fun getPlaceInfo(latitude: Double, longitude: Double): Single<Unit>
}

class StubRepository : ISearchParamsRepository {
    override fun setPassengersInfo() {
    }

    override fun setTripTypes() {
    }

    override fun getEmptySearchModel(): SearchModel {
        return SearchModel()
    }

    override fun search(chars: String): Single<Unit> {
        return Single.just(Unit)
    }

    override fun getPlaceInfo(latitude: Double, longitude: Double): Single<Unit> {
        return Single.just(Unit)
    }
}