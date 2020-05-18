package ru.movista.domain.usecase.tickets.searchparams

import io.reactivex.Single
import ru.movista.domain.model.tickets.Place

interface IPlaceSelectUseCase {

    fun getChosenPlaces(): Pair<Place?, Place?>

    fun searchPlaces(chars: String): Single<List<Place>>

    fun setFromPlace(place: Place)

    fun setToPlace(place: Place)
}