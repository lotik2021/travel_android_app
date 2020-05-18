package ru.movista.presentation.routedetail.delegate

import io.reactivex.disposables.CompositeDisposable
import ru.movista.presentation.Screens
import ru.movista.presentation.routedetail.RouteDetailPresenter
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.presentation.viewmodel.GoogleRouteDetailsViewModel
import ru.movista.presentation.viewmodel.TaxiViewModel
import ru.movista.presentation.viewmodel.TripViewModel
import ru.movista.utils.getExpectedItemFromList
import ru.movista.utils.schedulersIoToMain
import timber.log.Timber

class GoogleRouteDetailDelegate(
    private val routeDetailPresenter: RouteDetailPresenter,
    private val routeViewModel: GoogleRouteDetailsViewModel,
    private val baseTrips: List<TripViewModel>
) : RouteDetailTypeDelegate {

    private val viewState = routeDetailPresenter.viewState

    private val analyticsManager = routeDetailPresenter.analyticsManager

    private val router = routeDetailPresenter.router

    private val travelCardsManager = routeDetailPresenter.travelCardsManager

    private val travelCardsDisposable = CompositeDisposable()

    init {
        routeViewModel.apply {
            viewState.showGoogleRouteDetail(
                this,
                baseTrips,
                agencies.isNotEmpty()
            )
        }
    }

    override fun onMapReady() {
        routeViewModel.apply {
            if (trips.isNotEmpty()) viewState.showTripsOnMap(baseTrips)
        }
    }

    override fun onTrafficEnabled() {
        viewState.clearDrawnRoute()

        routeViewModel.apply {
            if (trips.isNotEmpty()) viewState.showTripsOnMap(
                trips = baseTrips,
                solidLine = false,
                showAllRoute = false
            )
        }
    }

    override fun onTrafficDisabled() {
        viewState.clearDrawnRoute()

        routeViewModel.apply {
            if (trips.isNotEmpty()) viewState.showTripsOnMap(
                trips = baseTrips,
                solidLine = true,
                showAllRoute = false
            )
        }
    }

    fun onTaxiProviderClick(tripIndex: Int, providerIndex: Int) {
        routeViewModel.let { routeDetailsViewModel ->

            val tripAtIndex = routeDetailsViewModel.trips[tripIndex]

            if (tripAtIndex !is TaxiViewModel) {
                Timber.e("Trip at selected index $tripIndex is not instance of TaxiViewModel")
                return
            }

            if (providerIndex > tripAtIndex.providers.lastIndex) {
                Timber.e("List of taxi providers $tripAtIndex doesn't contain element at $providerIndex index")
                return
            }

            val selectedTaxiTrip = tripAtIndex.providers[providerIndex]
            routeDetailPresenter.viewState.showTaxiOrderDialog(selectedTaxiTrip)
                .also {
                    analyticsManager
                        .reportRouteDetailsSelectTaxiTrip(selectedTaxiTrip.taxiProviderDescription)
                }

        }
    }

    fun onAgenciesInfoClicked() {
        routeViewModel.let { router.navigateTo(Screens.Agencies(it.agencies)) }
    }

    fun onRefillTravelCardFromFabClicked() {
        getAvailableTravelCards()
    }

    fun onViewTripOnMapClicked(index: Int) {
        val chosenTrip = getExpectedItemFromList<TripViewModel>(index, routeViewModel.trips)

        chosenTrip?.let {
            viewState.collapseBottomDialog()
            postOnMainThread { viewState.zoomInTrip(it.tripPolyline) }
        }
    }

    private fun getAvailableTravelCards() {
        travelCardsDisposable.clear()

        travelCardsDisposable.add(
            travelCardsManager.getAvailableTravelCards()
                .schedulersIoToMain()
                .doOnSubscribe {
                    viewState.showRefillTravelCardFabLoading()
                }
                .doAfterTerminate {
                    viewState.hideRefillTravelCardFabLoading()
                }
                .subscribe(
                    {
                        viewState.showRefillTravelCardDialog(it)
                    },
                    {
                        Timber.i(routeDetailPresenter.getErrorDescription(it))
                    }
                )
        )
    }
}