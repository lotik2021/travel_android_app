package ru.movista.presentation.routedetail

import android.content.res.Resources
import moxy.InjectViewState
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.framework.LocationManager
import ru.movista.data.framework.OrientationManager
import ru.movista.data.repository.TaxiOrderRepository
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.common.OnLocationPermissionResultListener
import ru.movista.presentation.common.OnMapEventsListener
import ru.movista.presentation.routedetail.delegate.CarRouteDetailDelegate
import ru.movista.presentation.routedetail.delegate.GoogleRouteDetailDelegate
import ru.movista.presentation.routedetail.delegate.RouteDetailTypeDelegate
import ru.movista.presentation.routedetail.delegate.TaxiRouteDetailDelegate
import ru.movista.presentation.utils.TravelCardsManager
import ru.movista.presentation.viewmodel.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

@InjectViewState
class RouteDetailPresenter(route: Serializable?) :
    BasePresenter<RouteDetailView>(),
    OnLocationPermissionResultListener,
    OnMapEventsListener {

    override val screenTag: String
        get() = Screens.RouteDetail.TAG

    private lateinit var routeViewModel: RouteDetailsViewModel

    private lateinit var routeDetailTypeDelegate: RouteDetailTypeDelegate

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var orientationManager: OrientationManager

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var travelCardsManager: TravelCardsManager

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var taxiOrderRepository: TaxiOrderRepository

    override fun onPresenterInject() {
        super.onPresenterInject()

        Injector.init(Screens.RouteDetail.TAG)
        Injector.routeDetailComponent?.inject(this)
    }

    init {
        if (route == null || route !is RouteDetailsViewModel) {
            Timber.e("Expected RouteViewModel but was $route")
        } else {
            routeViewModel = route
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        routeViewModel.apply {
            when (this) {
                is GoogleRouteDetailsViewModel -> {
                    routeDetailTypeDelegate =
                        GoogleRouteDetailDelegate(
                            this@RouteDetailPresenter,
                            this,
                            this.trips.filterIsInstance<TripViewModel>()
                        )
                }
                is CarRouteViewModel -> {
                    routeDetailTypeDelegate =
                        CarRouteDetailDelegate(
                            this@RouteDetailPresenter,
                            this
                        )
                }
                is TaxiProviderViewModel -> {
                    routeDetailTypeDelegate =
                        TaxiRouteDetailDelegate(
                            this@RouteDetailPresenter,
                            this,
                            resources
                        )
                }
            }
        }
    }

    override fun onLocationPermissionReceived(lat: Double, lon: Double) {
        locationManager.listenToLocationUpdatesIfPossible(true)
    }

    override fun onMapReady() {
        notifyDelegateOnMapReady()
    }

    override fun onMapTrafficEnabled() {
        routeDetailTypeDelegate.onTrafficEnabled()
    }

    override fun onMapTrafficDisabled() {
        routeDetailTypeDelegate.onTrafficDisabled()
    }

    fun onTaxiProviderClick(tripIndex: Int, providerIndex: Int) {
        (routeDetailTypeDelegate as? GoogleRouteDetailDelegate)?.onTaxiProviderClick(
            tripIndex,
            providerIndex
        )
    }

    fun onAgenciesInfoClicked() {
        (routeDetailTypeDelegate as? GoogleRouteDetailDelegate)?.onAgenciesInfoClicked()
    }

    fun onBackPressed(bottomDialogIsExpand: Boolean) {
        if (bottomDialogIsExpand) {
            viewState.collapseBottomDialog()
        } else {
            router.exit()
        }
    }

    fun onRefillTravelCardFromFabClicked() {
        (routeDetailTypeDelegate as? GoogleRouteDetailDelegate)?.onRefillTravelCardFromFabClicked()
    }


    fun onViewTripOnMapClicked(index: Int) {
        (routeDetailTypeDelegate as? GoogleRouteDetailDelegate)?.onViewTripOnMapClicked(index)
    }

    private fun notifyDelegateOnMapReady() {
        routeDetailTypeDelegate.onMapReady()
    }
}