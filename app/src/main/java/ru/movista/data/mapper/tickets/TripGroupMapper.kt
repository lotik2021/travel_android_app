package ru.movista.data.mapper.tickets

import ru.movista.data.entity.tickets.SearchResultsResponse
import ru.movista.di.FragmentScope
import ru.movista.domain.model.tickets.TripGroup
import ru.movista.domain.model.tickets.TripGroupData
import javax.inject.Inject

@FragmentScope
class TripGroupMapper @Inject constructor() {

    fun toTicketSearchResult(uid: String, entity: SearchResultsResponse): TripGroupData {
        val ticketSearchResultList = mutableListOf<TripGroup>()

        entity.data.pathGroups.forEach { group ->
            val ticketSearchResult = TripGroup(
                id = group.id,
                title = group.title,
                description = group.descr,
                minDuration = group.minDuration,
                minDurationTitle = group.minDurationTitle,
                minPrice = group.minPrice,
                minPriceTitle = group.minPriceTitle,
                tripTypeSequenceIcons = group.tripTypeSequence,
                state = group.state
            )

            ticketSearchResultList.add(ticketSearchResult)
        }

        return TripGroupData(uid, ticketSearchResultList)
    }
}