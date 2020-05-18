package ru.movista.data.repository

import io.reactivex.Single
import ru.movista.data.entity.AvailableTravelCardsResponse
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class TravelCardRepository @Inject constructor(private val api: Api, private val keyValueStorage: KeyValueStorage) {

    fun getAvailableTravelCards(): Single<AvailableTravelCardsResponse> {
        return api.getAvailableTravelCards(keyValueStorage.getToken())
    }
}