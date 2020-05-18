package ru.movista.presentation.tickets.segments.segmentfilter

import android.content.res.Resources
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import moxy.InjectViewState
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.source.local.models.*
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.usecase.tickets.SegmentsUseCase
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.tickets.segments.RouteFilterData
import ru.movista.presentation.tickets.segments.RouteItemPlaceFilter
import ru.movista.presentation.tickets.segments.RoutePlaceFilter
import ru.movista.presentation.tickets.segments.UserRouteFilter
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.utils.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max

@InjectViewState
class SegmentFilterPresenter(
    private val routes: List<Route>,
    oldUserRouteFilter: UserRouteFilter?,
    private val searchParams: SearchModel
) : BasePresenter<SegmentFilterView>() {
    companion object {
        private const val MIN_TRIP_TIME = 0L      // minutes
        private const val MAX_TRIP_TIME = 1439L   // minutes
        private const val TRANSFER_COUNT_FOR_PLACEHOLDER = 1
        private const val ROUTES_FILTERS_CALC_DELAY = 150L
    }

    override val screenTag: String
        get() = Screens.SegmentFilter.TAG

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var resources: Resources
    @Inject
    lateinit var segmentsUseCase: SegmentsUseCase
    @Inject
    lateinit var ticketsUseCase: TicketsUseCase
    @Inject
    lateinit var analyticsManager: AnalyticsManager

    private lateinit var filterData: RouteFilterData
    private var filteredRoutes: List<Route>? = null
    private var ticketsLifeTImeTimer: Disposable? = null
    private val routesSubject = PublishSubject.create<Unit>()
    private val userRouteFilter: UserRouteFilter

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.segmentsComponent?.inject(this)
    }

    init {
        setupFilters()

        userRouteFilter = oldUserRouteFilter?.copy() ?: UserRouteFilter(
            departureMinTime = MIN_TRIP_TIME.toLocalTime(),
            departureMaxTime = MAX_TRIP_TIME.toLocalTime(),
            arrivalMinTime = MIN_TRIP_TIME.toLocalTime(),
            arrivalMaxTime = MAX_TRIP_TIME.toLocalTime(),
            maxTripDuration = filterData.maxTripDuration,
            minPrice = filterData.minPrice,
            maxPrice = filterData.maxPrice,
            maxTransferCount = filterData.maxTransferCount,
            departurePoints = filterData.departurePoints,
            arrivalPoints = filterData.arrivalPoints,
            carriers = filterData.carriers
        )
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        subscribeToRoutesFilters()
        firstInitUiFilters()

        subscribeToRoutePlaceFilters()
        subscribeToTicketsLifeTImeTimer()
    }

    fun onDepartureRangeChanged(minTime: Int, maxTime: Int) {
        userRouteFilter.departureMinTime = minTime.plus(MIN_TRIP_TIME).toLocalTime()
        userRouteFilter.departureMaxTime = maxTime.plus(MIN_TRIP_TIME).toLocalTime()

        updateClearFiltersButtons()
        updateRoutes()
    }

    fun onArrivalRangeChanged(minTime: Int, maxTime: Int) {
        userRouteFilter.arrivalMinTime = minTime.plus(MIN_TRIP_TIME).toLocalTime()
        userRouteFilter.arrivalMaxTime = maxTime.plus(MIN_TRIP_TIME).toLocalTime()

        updateClearFiltersButtons()
        updateRoutes()
    }

    fun onMaxTripDurationChanged(index: Int) {
        viewState.setMaxTripDurationLabel(minutesToDDHHMM(index + filterData.minTripDuration))

        if (index.toLong() != filterData.maxTripDuration - filterData.minTripDuration) {
            viewState.setMaxTripDurationLabelColor(R.color.text_blue_medium)
        } else {
            viewState.setMaxTripDurationLabelColor(R.color.blue_primary)
        }

        userRouteFilter.maxTripDuration = index + filterData.minTripDuration

        updateClearFiltersButtons()
        updateRoutes()
    }

    fun onPriceRangeChanged(minPrice: Int, maxPrice: Int) {
        userRouteFilter.minPrice = minPrice.plus(filterData.minPrice)
        userRouteFilter.maxPrice = maxPrice.plus(filterData.minPrice)

        updateClearFiltersButtons()
        updateRoutes()
    }

    fun onMaxTransferCountChanged(transferCount: Int) {
        val text = if (transferCount == 0) {
            resources.getString(R.string.without_changes)
        } else {
            resources.getString(R.string.max_changes, transferCount)
        }

        viewState.setMaxTransferCountLabel(text)
        userRouteFilter.maxTransferCount = transferCount

        updateClearFiltersButtons()
        updateRoutes()
    }

    fun omResetAllClick() {
        onClearTimeClick()
        onClearRouteClick()
        onClearPointsClick()
        updateRoutes()
    }

    fun onClearPointsClick() {
        userRouteFilter.resetDeparturePoints()
        userRouteFilter.resetArrivalPoints()

        applyRouteFilters(RouteFilterType.DEPARTURE_POINTS, userRouteFilter.departurePoints)
        applyRouteFilters(RouteFilterType.ARRIVAL_POINTS, userRouteFilter.arrivalPoints)

        updateClearFiltersButtons()
        updateRoutes()
    }

    fun onClearTimeClick() {
        with(viewState) {
            setDepartureRangeBarIndex(MIN_TRIP_TIME, MAX_TRIP_TIME - MIN_TRIP_TIME)
            setArrivalRangeBarIndex(MIN_TRIP_TIME, MAX_TRIP_TIME - MIN_TRIP_TIME)
            setMaxTripDurationRangeBarIndex(filterData.maxTripDuration - filterData.minTripDuration)

            updateClearFiltersButtons()
            updateRoutes()
        }
    }

    fun onClearRouteClick() {
        with(viewState) {
            setPriceRangeBarIndex(0, filterData.maxPrice - filterData.minPrice)
            setMaxTransferCountRangeBarIndex(filterData.maxTransferCount)

            userRouteFilter.resetCarriers()
            applyRouteFilters(RouteFilterType.CARRIERS, userRouteFilter.carriers)

            updateClearFiltersButtons()
            updateRoutes()
        }
    }

    fun getFormattedPrice(tickIndex: Int): String {
        return if (filterData.maxPrice == filterData.minPrice) {
            filterData.minPrice.toPriceFormat()
        } else {
            (tickIndex + filterData.minPrice).toPriceFormat()
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onCarriersFilterClick() {
        router.navigateTo(Screens.SelectableDataFilter(
            RouteFilterType.CARRIERS,
            userRouteFilter.carriers,
            searchParams
        ))
    }

    fun onDeparturePointsFilterClick() {
        router.navigateTo(Screens.SelectableDataFilter(
            RouteFilterType.DEPARTURE_POINTS,
            userRouteFilter.departurePoints,
            searchParams
        ))
    }

    fun onArrivalPointsFilterClick() {
        router.navigateTo(Screens.SelectableDataFilter(
            RouteFilterType.ARRIVAL_POINTS,
            userRouteFilter.arrivalPoints,
            searchParams
        ))
    }

    fun getFormattedTransferCount(tickIndex: Int): String {
        return if (filterData.maxTransferCount == 0) {
            ""
        } else {
            tickIndex.toString()
        }
    }

    fun onApplyClick() {
        filteredRoutes?.let {
            segmentsUseCase.applyFilterSelected(userRouteFilter, it)
            router.exit()

            analyticsManager.reportSetFilter(routes.size != it.size)
        }
    }

    fun onRepeatExpiredSearchClick() {
        router.backTo(Screens.SearchTickets())
        router.navigateTo(Screens.TripGroupScreen(searchParams))
    }

    fun onCancelSearchClick() {
        router.backTo(Screens.SearchTickets())
    }

    override fun onDestroy() {
        ticketsLifeTImeTimer?.dispose()
        super.onDestroy()
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

    private fun subscribeToRoutePlaceFilters() {
        addDisposable(
            segmentsUseCase.getRoutePlaceFilterObservable()
                .subscribe { filterPair ->
                    applyRouteFilters(filterPair.first, filterPair.second)
                    updateRoutes()
                    updateClearFiltersButtons()
                }
        )
    }

    private fun applyRouteFilters(routeFilterType: RouteFilterType, routes: List<RoutePlaceFilter>) {
        val totalFilters = routes.sumBy { it.routeItems.size }
        val selectedFilters = routes.sumBy { placeFilter ->
            placeFilter.routeItems.filter { it.isSelected }.count()
        }

        val isSelectedFilters = totalFilters != selectedFilters

        val filterLabelText = if (isSelectedFilters) {
            selectedFilters.toString()
        } else {
            resources.getString(R.string.all)
        }

        when (routeFilterType) {
            RouteFilterType.ARRIVAL_POINTS -> {
                userRouteFilter.arrivalPoints = routes
                viewState.setArrivalPointsLabelText(filterLabelText)
                viewState.setArrivalPointsLabelEnabled(isSelectedFilters)
            }
            RouteFilterType.DEPARTURE_POINTS -> {
                userRouteFilter.departurePoints = routes
                viewState.setDeparturePointsLabelText(filterLabelText)
                viewState.setDeparturePointsLabelEnabled(isSelectedFilters)
            }
            RouteFilterType.CARRIERS -> {
                userRouteFilter.carriers = routes
                viewState.setCarriersLabelText(filterLabelText)
                viewState.setCarriersLabelEnabled(isSelectedFilters)
            }
        }
    }

    private fun updateRoutes() {
        routesSubject.onNext(Unit)
    }

    private fun setupFilters() {
        val tripDurations = mutableListOf<Long>()
        val prices = mutableListOf<Int>()
        var maxTransferCount = 0
        val carriers = mutableListOf<Pair<String, ObjectTripType>>()
        val departurePoints = mutableListOf<Pair<String, ObjectTripType>>()
        val arrivalPoints = mutableListOf<Pair<String, ObjectTripType>>()

        routes.forEach { route ->
            tripDurations.add(route.totalDuration)
            prices.addAll(getPrices(route))

            maxTransferCount = max(maxTransferCount, route.transferCount)

            carriers.addAll(
                route.tripsToServices
                    .filterNot { it.trip.objectType.isTransfer() }
                    .map { it.trip }
                    .map {
                        if (it.objectType == ObjectTripType.FLIGHT) {
                            (it.opCarrierName ?: "") to it.objectType
                        } else {
                            (it.carrierName ?: "") to it.objectType
                        }
                    }
            )

            val firstRoute = route.tripsToServices
                .filterNot { it.trip.objectType.isTransfer() }
                .map { it.trip }
                .first()

            val lastRoute = route.tripsToServices
                .filterNot { it.trip.objectType.isTransfer() }
                .map { it.trip }
                .last()

            departurePoints.add((firstRoute.fromDescription ?: "") to firstRoute.objectType)
            arrivalPoints.add((lastRoute.toDescription ?: "") to lastRoute.objectType)
        }

        val carriersFilter = getRoutePlaceFilters(carriers) { getCarriersTitle(it) }
        val departurePointsFilter = getRoutePlaceFilters(departurePoints) { getPlacesTitle(it) }
        val arrivalPointsFilter = getRoutePlaceFilters(arrivalPoints) { getPlacesTitle(it) }

        filterData = RouteFilterData(
            maxTripDuration = tripDurations.max() ?: 0L,
            minTripDuration = tripDurations.min() ?: 0L,
            maxPrice = prices.max() ?: 0,
            minPrice = prices.min() ?: 0,
            maxTransferCount = maxTransferCount,
            carriers = carriersFilter,
            departurePoints  = departurePointsFilter,
            arrivalPoints = arrivalPointsFilter
        )
    }

    private fun getRoutePlaceFilters(
        data: List<Pair<String, ObjectTripType>>,
        getTitle: (ObjectTripType) -> String
    ): List<RoutePlaceFilter> {
        val distinctData = data.distinctBy { it.first }
        return ObjectTripType.values()
            .map { tripType ->
                RoutePlaceFilter(
                    title = getTitle.invoke(tripType),
                    tripType = tripType,
                    routeItems = distinctData
                        .filter { it.second == tripType }
                        .map { RouteItemPlaceFilter(it.first) }
                        .sortedBy { it.name }
                )
            }
            .filter { it.routeItems.isNotEmpty() }
    }

    private fun getCarriersTitle(tripType: ObjectTripType): String {
        return when(tripType) {
            ObjectTripType.FLIGHT -> resources.getString(R.string.plane_small)
            ObjectTripType.TRAIN -> resources.getString(R.string.train_small)
            else -> resources.getString(R.string.bus_small)
        }.toDefaultLowerCase()
    }

    private fun getPlacesTitle(tripType: ObjectTripType): String {
        return when(tripType) {
            ObjectTripType.FLIGHT -> resources.getString(R.string.airports)
            ObjectTripType.TRAIN -> resources.getString(R.string.railway_stations)
            else -> resources.getString(R.string.stations)
        }.toDefaultLowerCase()
    }

    private fun firstInitUiFilters() {
        with(viewState) {
            if (filterData.maxTripDuration == filterData.minTripDuration) {
                disableMaxTripDurationRangeBar()
                setMaxTripDurationLabel(minutesToDDHHMM(filterData.minTripDuration))
            } else {
                setupMaxTripDurationRangeBar(filterData.minTripDuration, filterData.maxTripDuration)
                setTripDurationLabels(
                    minutesToDDHHMM(filterData.minTripDuration),
                    minutesToDDHHMM(filterData.maxTripDuration)
                )
            }

            if (filterData.maxPrice == filterData.minPrice) {
                disablePriceRangeBar()
            } else {
                setupPriceRangeBar(filterData.minPrice, filterData.maxPrice)
            }

            if (filterData.maxTransferCount == 0) {
                disableMaxTransferCountRangeBar()
                setupMaxTransferCountRangeBar(TRANSFER_COUNT_FOR_PLACEHOLDER)
                setMaxTransferCountLabel(resources.getString(R.string.without_changes))
            } else {
                setupMaxTransferCountRangeBar(filterData.maxTransferCount)
            }

            setupDepartureRangeBar(MIN_TRIP_TIME, MAX_TRIP_TIME)
            setupArrivalRangeBar(MIN_TRIP_TIME, MAX_TRIP_TIME)

            enableRangeBarsListeners()

            setMaxTripDurationRangeBarIndex(userRouteFilter.maxTripDuration - filterData.minTripDuration)
            setMaxTransferCountRangeBarIndex(userRouteFilter.maxTransferCount)
            setPriceRangeBarIndex(
                userRouteFilter.minPrice - filterData.minPrice,
                userRouteFilter.maxPrice - filterData.minPrice
            )
            setDepartureRangeBarIndex(
                userRouteFilter.departureMinTimeMinutes(),
                userRouteFilter.departureMaxTimeMinutes() - MIN_TRIP_TIME
            )
            setArrivalRangeBarIndex(
                userRouteFilter.arrivalMinTimeMinutes(),
                userRouteFilter.arrivalMaxTimeMinutes() - MIN_TRIP_TIME
            )

            applyRouteFilters(RouteFilterType.CARRIERS, userRouteFilter.carriers)
            applyRouteFilters(RouteFilterType.DEPARTURE_POINTS, userRouteFilter.departurePoints)
            applyRouteFilters(RouteFilterType.ARRIVAL_POINTS, userRouteFilter.arrivalPoints)

            updateRoutes()
        }
    }

    private fun subscribeToRoutesFilters() {
        addDisposable(routesSubject.debounce(ROUTES_FILTERS_CALC_DELAY, TimeUnit.MILLISECONDS)
            .doOnNext { postOnMainThread { viewState.setApplyBtnEnabled(false) } }
            .switchMapSingle {
                Single.just(routes)
                    .map { routes ->
                        routes.filter { route ->
                            route.departureDate?.isAfterMinutes(userRouteFilter.departureMinTime) == true
                                    && route.departureDate.isBeforeMinutes(userRouteFilter.departureMaxTime) == true
                        }
                    }
                    .map { routes ->
                        routes.filter { route ->
                                route.arrivalDate?.isAfterMinutes(userRouteFilter.arrivalMinTime) == true
                                        && route.arrivalDate.isBeforeMinutes(userRouteFilter.arrivalMaxTime) == true
                        }
                    }
                    .map { routes ->
                        routes.filter { route ->
                            route.transferCount <= userRouteFilter.maxTransferCount
                        }
                    }
                    .map { routes ->
                        routes.filter { route ->
                            route.totalDuration <= userRouteFilter.maxTripDuration
                        }
                    }
                    .map { routes ->
                        routes.filter { route ->
                            getPrices(route).any { price ->
                                price >= userRouteFilter.minPrice && price <= userRouteFilter.maxPrice
                            }
                        }
                    }
                    .map { routes ->
                        val selectedCarriers = userRouteFilter.selectedCarriersNames()

                        routes.filter { route ->
                            route.tripsToServices
                                .map { it.trip }
                                .any {
                                    if (it.objectType == ObjectTripType.FLIGHT) {
                                        selectedCarriers.contains(it.opCarrierName)
                                    } else {
                                        selectedCarriers.contains(it.carrierName)
                                    }
                                }
                        }
                    }
                    .map { routes ->
                        val selectedDeparturePoints = userRouteFilter.selectedDeparturePointsNames()

                        routes.filter { route ->
                            val firstRoute = route.tripsToServices
                                .map { it.trip }
                                .first()

                            selectedDeparturePoints.contains(firstRoute.fromDescription)
                        }
                    }
                    .map { routes ->
                        val selectedArrivalPoints = userRouteFilter.selectedArrivalPointsNames()

                        routes.filter { route ->
                            val lastRoute = route.tripsToServices
                                .map { it.trip }
                                .last()

                            selectedArrivalPoints.contains(lastRoute.toDescription)
                        }
                    }
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredRoutes ->
                    this.filteredRoutes = filteredRoutes

                    val minPrice = filteredRoutes
                        .map { route -> getPrices(route) }
                        .flatten()
                        .min() ?: 0

                    viewState.setApplyBtnText(resources.getQuantityString(
                        R.plurals.filter_applying_title,
                        filteredRoutes.size,
                        filteredRoutes.size,
                        minPrice.toPriceFormat())
                    )
                    viewState.setApplyBtnEnabled(true)
                },
                { th ->
                    Timber.wtf(th, "error updateRoutes")
                }
            )
        )
    }

    private fun getPrices(route: Route) : List<Int> {
        val alternatives = route.tripsToServices
            .filter { it.service.objectType.isTrain() }
            .mapNotNull { it.service.alternatives
                ?.filter { coachType -> coachType.coachType.isValid() }
            }
            .flatten()

        return if (alternatives.isEmpty()) {
            arrayListOf(route.price.toInt())
        } else {
            alternatives.mapNotNull { it.price?.toInt() }
        }
    }

    private fun updateClearFiltersButtons() {
        viewState.setResetTimeBtnVisibility(hasTimeFilters())
        viewState.setResetRouteBtnVisibility(hasRouteFilter())
        viewState.setResetPointsBtnVisibility(hasPointsFilter())
        viewState.setAllResetBtnsVisibility(hasAnyFilter())
    }

    private fun hasTimeFilters(): Boolean {
        return (userRouteFilter.departureMaxTimeMinutes() != MAX_TRIP_TIME
                || userRouteFilter.departureMinTimeMinutes() != MIN_TRIP_TIME
                || userRouteFilter.arrivalMaxTimeMinutes() != MAX_TRIP_TIME
                || userRouteFilter.arrivalMinTimeMinutes() != MIN_TRIP_TIME
                || userRouteFilter.maxTripDuration != filterData.maxTripDuration)
    }

    private fun hasRouteFilter(): Boolean {
        return (
                userRouteFilter.minPrice != filterData.minPrice
                        || userRouteFilter.maxPrice != filterData.maxPrice
                        || userRouteFilter.maxTransferCount != filterData.maxTransferCount
                        || userRouteFilter.totalCarriersSize() != userRouteFilter.selectedCarriersSize()
                )
    }

    private fun hasPointsFilter(): Boolean {
        return  (userRouteFilter.totalDeparturePointsSize() != userRouteFilter.selectedDeparturePointsSize()
                || userRouteFilter.totalArrivalPointsSize() != userRouteFilter.selectedArrivalPointsSize())
    }

    private fun hasAnyFilter(): Boolean {
        return hasRouteFilter() || hasTimeFilters() || hasPointsFilter()
    }
}