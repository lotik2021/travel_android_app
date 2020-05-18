package ru.movista.presentation.utils

import io.reactivex.Single
import ru.movista.data.entity.AvailableTravelCardsResponse
import ru.movista.data.repository.TravelCardRepository
import ru.movista.di.FragmentScope
import ru.movista.presentation.converter.TravelCardsConverter
import ru.movista.presentation.refilltravelcard.TravelCardViewModel
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class TravelCardsManager @Inject constructor(
    private val travelCardRepository: TravelCardRepository,
    private val travelCardsConverter: TravelCardsConverter
) {
    fun getAvailableTravelCards(): Single<ArrayList<TravelCardViewModel>> {
        Timber.i("getting available travel cards")
        return travelCardRepository.getAvailableTravelCards()
            .map {
                travelCardsConverter.toTravelCardsViewModel(it)
            }
    }

    fun getAvailableTravelCardsFromTravelCardsResponse(travelCardsResponse: AvailableTravelCardsResponse): ArrayList<TravelCardViewModel> {
        return travelCardsConverter.toTravelCardsViewModel(travelCardsResponse)
    }
}