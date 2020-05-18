package ru.movista.domain.usecase

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import ru.movista.data.entity.SearchPlaceResponse
import ru.movista.data.repository.SearchPlaceRepository
import ru.movista.di.FragmentScope
import ru.movista.domain.model.FavoritePlace
import ru.movista.domain.model.Place
import javax.inject.Inject

interface PlaceFoundUseCase {
    fun onPlaceFound(place: Place)
    fun onFindCanceled()
}

interface IChoosePlaceUseCase {
    fun getOnPlaceFoundObservable(): Observable<Place>
    fun getOnFavoritePlaceFoundObservable(): Observable<FavoritePlace>
}

interface SearchPlaceUseCase {
    fun searchPlace(query: String): Single<SearchPlaceResponse>
    fun getPlaceByID(placeID: String, placeName: String, placeDescription: String): Single<Place>
    fun getPlace(placeID: String, placeName: String, placeDescription: String): Single<Place>
    fun sendChosenHistoryPlace(place: Place)
    fun sendChosenFavoritePlace(favoritePlace: FavoritePlace)
    fun setSessionToken(token: String)
}

@FragmentScope
class ChoosePlaceUseCase @Inject constructor(
    private val searchPlaceRepository: SearchPlaceRepository
) : PlaceFoundUseCase, IChoosePlaceUseCase, SearchPlaceUseCase {
    private val placeFoundSubject = PublishSubject.create<Place>()
    private val favoritePlaceFoundSubject = PublishSubject.create<FavoritePlace>()

    override fun onPlaceFound(place: Place) {
        emitPlaceFoundEvent(place)
    }

    override fun onFindCanceled() {
        emitFindCanceledEvent()
    }

    override fun getOnPlaceFoundObservable(): Observable<Place> {
        return placeFoundSubject
    }

    override fun getOnFavoritePlaceFoundObservable(): Observable<FavoritePlace> {
        return favoritePlaceFoundSubject
    }

    override fun searchPlace(query: String): Single<SearchPlaceResponse> {
        return searchPlaceRepository.searchPlace(query)
    }

    override fun getPlaceByID(placeID: String, placeName: String, placeDescription: String): Single<Place> {
        return searchPlaceRepository.getPlaceByID(placeID)
            .map { it.copy(name = placeName, description = placeDescription) }
            .doOnSuccess {
                emitPlaceFoundEvent(it)
            }
    }

    override fun getPlace(placeID: String, placeName: String, placeDescription: String): Single<Place> {
        return searchPlaceRepository.getPlaceByID(placeID)
            .map { it.copy(name = placeName, description = placeDescription) }
    }

    override fun sendChosenHistoryPlace(place: Place) {
        emitPlaceFoundEvent(place)
    }

    override fun sendChosenFavoritePlace(favoritePlace: FavoritePlace) {
        emitFavoritePlaceFoundEvent(favoritePlace)
    }

    override fun setSessionToken(token: String) {
        searchPlaceRepository.currentGoogleSessionToken = token
    }

    private fun emitPlaceFoundEvent(place: Place) {
        placeFoundSubject.onNext(place)
    }

    private fun emitFavoritePlaceFoundEvent(place: FavoritePlace) {
        favoritePlaceFoundSubject.onNext(place)
    }

    private fun emitFindCanceledEvent() {
        placeFoundSubject.onError(Throwable("Find place was canceled"))
    }
}