package ru.movista.presentation.converter

import android.content.res.Resources
import ru.movista.di.FragmentScope
import ru.movista.presentation.viewmodel.*
import ru.movista.utils.durationBetweenTime
import javax.inject.Inject

@FragmentScope
class RouteViewModelConverter @Inject constructor(val resources: Resources) {

    fun routeToRouteDetailsViewModel(routeViewModel: RouteViewModel): GoogleRouteDetailsViewModel {

        val tripDetailsViewModel = arrayListOf<TripDetailsViewModel>()

        tripDetailsViewModel.addAll(routeViewModel.trips)

        insertTransitions(tripDetailsViewModel)

        tripDetailsViewModel.add(DestinationDetailViewModel(routeViewModel.destinationName, routeViewModel.endTime))

        return with(routeViewModel) {
            GoogleRouteDetailsViewModel(
                id,
                departureName,
                destinationName,
                endTime,
                durationMinutes,
                durationWithHours,
                startToEndTime,
                minPrice,
                tripDetailsViewModel,
                agencies
            )
        }
    }

    private fun insertTransitions(sourceList: ArrayList<TripDetailsViewModel>) {
        var previousTripEntity: TripViewModel? = null

        var itemAddedCount = 0

        val copy = arrayListOf<TripViewModel>().apply {
            addAll(sourceList.filterIsInstance<TripViewModel>())
        }

        copy.forEachIndexed { index, tripViewModel ->

            previousTripEntity?.let { previousTripEntity ->

                if (
                    previousTripEntity::class == tripViewModel::class
                    && previousTripEntity !is OnFootViewModel
                    && previousTripEntity !is TaxiViewModel
                ) {
                    sourceList.add(
                        index + itemAddedCount,
                        TransitViewModel(
                            durationBetweenTime(previousTripEntity.tripEndTime, tripViewModel.tripStartTime)
                        )
                    )
                    itemAddedCount++
                }
            }
            previousTripEntity = tripViewModel
        }

    }
}