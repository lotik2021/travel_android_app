package ru.movista.presentation.tickets.segments

import android.content.res.Resources
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import org.threeten.bp.LocalDate
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.source.local.models.ObjectTripType
import ru.movista.data.source.local.models.TicketSortingOption
import ru.movista.data.source.local.models.isNotTransfer
import ru.movista.data.source.local.models.isTrain
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.usecase.tickets.SegmentsUseCase
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.tickets.detailroute.DetailRoutesMode
import ru.movista.utils.formatByPattern
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SegmentsPresenter(
    private val segmentArgumentsWrapper: SegmentArgumentsWrapper
) : BasePresenter<SegmentsView>() {

    override val screenTag: String
        get() = Screens.Segments.TAG

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var segmentsUseCase: SegmentsUseCase
    @Inject
    lateinit var resources: Resources
    @Inject
    lateinit var ticketsUseCase: TicketsUseCase
    @Inject
    lateinit var analyticsManager: AnalyticsManager

    private var ticketsLifeTImeTimer: Disposable? = null
    private var userRouteFilter: UserRouteFilter? = null
    private val datePattern = "dd MMMM"
    private val filteredRoutes = mutableListOf<Route>()

    private var ticketSortingOption = TicketSortingOption.CHEAPER_FIRST
    private val routes: List<Route> = segmentArgumentsWrapper.routes
    private val isReturn: Boolean = segmentArgumentsWrapper.isReturn
    private val searchParams: SearchModel = segmentArgumentsWrapper.searchParams
    private val tripDate: LocalDate?

    init {
        tripDate = if (isReturn) searchParams.arrivalDate else searchParams.departureDate
    }

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.segmentsComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        with(viewState) {
            filteredRoutes.addAll(routes)

            val date = tripDate?.formatByPattern(datePattern) ?: ""
            val title = if (isReturn) {
                resources.getString(R.string.basket_header_back, date)
            } else {
                resources.getString(R.string.basket_header_forth, date)
            }

            setTitle(title)

            if (routes.isNullOrEmpty()) {
                viewState.showEmptyRoutesMessage()
                viewState.setFiltersVisibility(false)
            } else {
                if (routes.size == 1) {
                    viewState.setFiltersVisibility(false)
                }

                updateTickets(getRotesSortedBy(routes, TicketSortingOption.CHEAPER_FIRST))
            }
        }

        subscribeToTicketsLifeTImeTimer()
        subscribeToFilters()
    }

    fun onBackClicked() {
        router.exit()
        analyticsManager.reportBackwardsClick("segment_tickets")
    }

    fun onFilterClick() {
        router.navigateTo(
            Screens.SegmentFilter(
                routes,
                userRouteFilter,
                segmentArgumentsWrapper.searchParams
            )
        )
        analyticsManager.reportOpenFilter()
    }

    fun onSortClick() {
        viewState.openSortingOptions(ticketSortingOption)
    }

    fun onRouteClick(route: Route) {
        segmentArgumentsWrapper.route = route

        val detailRoutesMode =
            if (segmentArgumentsWrapper.isSkipBasket) DetailRoutesMode.BUYING
            else DetailRoutesMode.SELECTING

        router.navigateTo(Screens.DetailRoutes(segmentArgumentsWrapper, detailRoutesMode))

        reportOpenTicketDetails(route)
    }

    fun onRepeatExpiredSearchClick() {
        router.backTo(Screens.SearchTickets())
        router.navigateTo(Screens.TripGroupScreen(segmentArgumentsWrapper.searchParams))
    }

    fun onCancelSearchClick() {
        router.backTo(Screens.SearchTickets())
    }

    fun onEmptyRoutesBtnClick() {
        router.exit()
    }

    fun onSortingOptionClick(option: TicketSortingOption) {
        ticketSortingOption = option
        viewState.updateTickets(getRotesSortedBy(routes, option))
    }

    override fun onDestroy() {
        ticketsLifeTImeTimer?.dispose()
        super.onDestroy()
    }

    private fun reportOpenTicketDetails(route: Route) {
        val firstTripsToServices = route.tripsToServices.firstOrNull { it.trip.objectType.isNotTransfer() }
        val carriers = route.tripsToServices.joinToString {
            if (it.trip.objectType == ObjectTripType.FLIGHT)
                it.trip.opCarrierName.toString()
            else
                it.trip.carrierName.toString()
        }

        firstTripsToServices?.let {
            analyticsManager.reportOpenTicketDetails(
                tripType = firstTripsToServices.trip.tripType.id,
                carrier = carriers,
                totalDuration = route.totalDuration,
                price = route.price,
                transferCount = route.transferCount,
                position = getRotesSortedBy(routes, ticketSortingOption).indexOf(route),
                isFiltered = filteredRoutes.size != routes.size
            )
        }
    }

    private fun subscribeToFilters() {
        addDisposable(
            segmentsUseCase.getFilterSelectedObservable()
                .subscribe { filterPair ->
                    filteredRoutes.clear()
                    filteredRoutes.addAll(filterPair.second)

                    userRouteFilter = filterPair.first
                    viewState.updateTickets(
                        getRotesSortedBy(
                            filterPair.second,
                            ticketSortingOption
                        )
                    )
                }
        )
    }

    private fun subscribeToTicketsLifeTImeTimer() {
        if (ticketsLifeTImeTimer == null) {
            ticketsLifeTImeTimer = ticketsUseCase.getTicketsLifetimeTimer()
                .subscribe(
                    {
                        viewState.showTicketsExpiredTimer()
                        Timber.tag("TicketsLifeTImeTimer").d("TicketsLifeTImeTimer is expired")
                    },
                    { th ->
                        Timber.tag("TicketsLifeTImeTimer").e(th, "TicketsLifeTImeTimer failed")
                    }
                )
        }
    }

    private fun getRotesSortedBy(
        routes: List<Route>,
        sortingOption: TicketSortingOption
    ): List<Route> {
        return when (sortingOption) {
            TicketSortingOption.CHEAPER_FIRST -> routes.sortedBy {
                val service = it.tripsToServices.firstOrNull()?.service

                if (service?.objectType.isTrain()) {
                    service?.alternatives
                        ?.mapNotNull { alternative -> alternative.price }
                        ?.min() ?: it.price
                } else {
                    it.price
                }
            }
            TicketSortingOption.EXPENSIVE_FIRST -> routes.sortedByDescending {
                val service = it.tripsToServices.firstOrNull()?.service

                if (service?.objectType.isTrain()) {
                    service?.alternatives
                        ?.mapNotNull { alternative -> alternative.price }
                        ?.max() ?: it.price
                } else {
                    it.price
                }
            }
            TicketSortingOption.EARLY_ARRIVAL -> routes.sortedBy { it.arrivalDate }
            TicketSortingOption.LATE_ARRIVAL -> routes.sortedByDescending { it.arrivalDate }
            TicketSortingOption.EARLY_DEPARTURE -> routes.sortedBy { it.departureDate }
            TicketSortingOption.LATE_DEPARTURE -> routes.sortedByDescending { it.departureDate }
            TicketSortingOption.TRAVEL_DURATION -> routes.sortedBy { it.totalDuration }
        }
    }
}