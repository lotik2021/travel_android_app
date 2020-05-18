package ru.movista.presentation.converter

import android.content.res.Resources
import ru.movista.data.entity.AvailableTravelCardsResponse
import ru.movista.di.FragmentScope
import ru.movista.presentation.refilltravelcard.StrelkaViewModel
import ru.movista.presentation.refilltravelcard.TravelCardViewModel
import ru.movista.presentation.refilltravelcard.TroikaViewModel
import ru.movista.data.source.local.TravelCardType
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class TravelCardsConverter @Inject constructor(private val resources: Resources) {

    fun toTravelCardsViewModel(availableTravelCardsResponse: AvailableTravelCardsResponse): ArrayList<TravelCardViewModel> {
        val travelCards = arrayListOf<TravelCardViewModel>()
        availableTravelCardsResponse.travel_cards.forEach {
            if (it.id == TravelCardType.TROIKA) {
                if (it.maximum_amount != null && it.minimum_amount != null) {
                    travelCards.add(
                        TroikaViewModel(
                            it.id,
                            it.name,
                            it.description,
                            it.warn,
                            it.maximum_card_length,
                            it.minimum_card_length,
                            it.maximum_amount,
                            it.minimum_amount
                        )
                    )
                } else {
                    Timber.e("Troika received amount is null")
                }
            } else if (it.id == TravelCardType.STRELKA) {
                travelCards.add(
                    StrelkaViewModel(
                        it.id,
                        it.name,
                        it.description,
                        it.warn,
                        it.maximum_card_length,
                        it.minimum_card_length
                    )
                )
            }
        }

        return travelCards
    }
}
