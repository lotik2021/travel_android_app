package ru.movista.presentation.tickets.basket

import android.content.res.Resources
import android.view.View
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import moxy.InjectViewState
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.source.local.models.BasketItemState
import ru.movista.data.source.local.models.TripType
import ru.movista.data.source.local.models.isTrain
import ru.movista.data.source.local.models.toTripType
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.*
import ru.movista.domain.usecase.tickets.BasketUseCase
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.tickets.detailroute.DetailRoutesMode
import ru.movista.presentation.tickets.segments.SegmentArgumentsWrapper
import ru.movista.presentation.viewmodel.SegmentViewModel
import ru.movista.utils.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class BasketPresenter(
    private val searchParams: SearchModel,
    private val searchUid: String,
    private val tripGroup: TripGroup,
    private val currentPathGroups: PathGroup
) : BasePresenter<BasketView>() {
    companion object {
        private const val TIME_PATTERN = "H:mm"
        private const val DATE_PATTERN = "dd LLL"
    }

    override val screenTag: String
        get() = Screens.Basket.TAG

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var basketUseCase: BasketUseCase
    @Inject
    lateinit var resources: Resources
    @Inject
    lateinit var ticketsUseCase: TicketsUseCase
    @Inject
    lateinit var analyticsManager: AnalyticsManager

    private val segmentsViewModels = arrayListOf<SegmentViewModel>()
    private var selectingRoutDisposable = Disposables.disposed()
    private var ticketsLifeTImeTimer: Disposable? = null

    private val datePattern = "dd LLL"

    val isShowBasketHint: Boolean

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.basketComponent?.inject(this)
    }

    init {
        isShowBasketHint = basketUseCase.isShowBasketHint()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        with(viewState) {
            setRoutsIcons(tripGroup.tripTypeSequenceIcons)

            val forthDateTitle = resources.getString(
                R.string.basket_header_forth,
                searchParams.departureDate?.formatByPattern(datePattern)
            )
            val backDateTitle =
                if (searchParams.arrivalDate == null) null
                else resources.getString(
                    R.string.basket_header_back,
                    searchParams.arrivalDate?.formatByPattern(datePattern)
                )

            setDate(forthDateTitle, backDateTitle)
        }

        loadSegments()
        subscribeToTicketsLifeTImeTimer()
    }

    override fun attachView(view: BasketView) {
        super.attachView(view)
        updateBasket()
    }

    fun onBackClicked() {
        router.exit()
        analyticsManager.reportBackwardsClick("segments")
    }

    fun onNotShowBasketHint() {
        basketUseCase.saveDisablingBasketHint()
        viewState.removeBasketHint()
    }

    fun onSegmentClick(segmentViewModel: SegmentViewModel) {
        getSegmentByBasketViewModel(segmentViewModel)?.let { segment ->
            if (segment.basketItemState == BasketItemState.SELECTED) {
                onTripDetailClick(segmentViewModel)
            } else if (segment.basketItemState == BasketItemState.NOT_SELECTED) {
                if (segment == currentPathGroups.segments.first()) {
                    navigateToChooseSegment(segment)
                } else {
                    loadOtherSegment(segment)
                }

                analyticsManager.reportRouteSegmentClick(
                    departureDate = searchParams.departureDate.toString(),
                    tripType = segment.tripTypes.joinToString { it.id }
                )
            }
        }
    }

    fun onOptionItemClick(item: SegmentViewModel) {
        viewState.openSegmentOptions(item)
    }

    fun onTripChangeClick(segmentModel: SegmentViewModel) {
        getSegmentByBasketViewModel(segmentModel)?.let { navigateToChooseSegment(it) }
    }

    fun onTripDetailClick(segmentViewModel: SegmentViewModel) {
        getSegmentByBasketViewModel(segmentViewModel)?.let { segment ->
            subscribeToRoutSelecting(segment)

            router.navigateTo(
                Screens.DetailRoutes(
                    SegmentArgumentsWrapper(
                        searchParams = searchParams,
                        routes = segment.routes,
                        route = segment.selectedRoute,
                        tripPlaces = currentPathGroups.tripPlaces,
                        segments = currentPathGroups.segments,
                        groupId = tripGroup.id,
                        searchUid = searchUid,
                        isReturn = segment.isReturn
                    ),
                    DetailRoutesMode.SHOVING
                )
            )
        }
    }

    fun onBuyBtnClick() {
        addDisposable(basketUseCase.saveSelectedRoutes(
            searchUid,
            tripGroup.id,
            currentPathGroups.segments
        )
            .schedulersIoToMain()
            .doOnSubscribe {
                viewState.setLoaderVisibility(true)
                viewState.setBuyBtnVisibility(false)
            }
            .doAfterTerminate {
                viewState.setLoaderVisibility(false)
                viewState.setBuyBtnVisibility(true)
            }
            .subscribe(
                { response ->
                    response.actualUrl?.let { url ->
                        viewState.openExternalUrl(url)
                        reportBuyRoute(url)
                    }
                },
                { th ->
                    viewState.showSimpleErrorMsg(getErrorDescription(th))
                    Timber.e(th, "getSegmentRoutes failed")
                }
            )
        )
    }

    fun onRepeatExpiredSearchClick() {
        router.backTo(Screens.SearchTickets())
        router.navigateTo(Screens.TripGroupScreen(searchParams))
    }

    fun onCancelSearchClick() {
        router.backTo(Screens.SearchTickets())
    }

    private fun loadSegments() {
        for (segment in currentPathGroups.segments) {
            if (segment.tripTypes.isEmpty()) {
                continue
            }

            if (segment.isOnlyTaxi()) {
                segment.basketItemState = BasketItemState.SELECTED
            }

            val emptyBasketItem = SegmentViewModel(
                id = segment.id,
                title = createNotSelectedSegmentTitle(segment.tripTypes),
                description = segment.name,
                isReturnTrip = segment.isReturn,
                tripTypes = segment.tripTypes,
                subDescription = "",
                basketItemState = segment.basketItemState
            )

            segmentsViewModels.add(emptyBasketItem)
        }

        viewState.updateSegments(segmentsViewModels)
    }

    private fun getSegmentByBasketViewModel(segmentViewModel: SegmentViewModel) : Segment? {
        return currentPathGroups.segments.firstOrNull { it.id == segmentViewModel.id }
    }

    private fun loadOtherSegment(segment: Segment) {
        if (segment.routes.isNotEmpty()) {
            navigateToChooseSegment(segment)
        } else {
            addDisposable(basketUseCase.getSegmentRoutes(
                uid = searchUid,
                groupId = tripGroup.id,
                segmentId = segment.id,
                selectedRouteIds = currentPathGroups.segments.mapNotNull { it.selectedRoute?.id }
            )
                .schedulersIoToMain()
                .doOnSubscribe {
                    viewState.hideErrorMsg()
                    viewState.setLoaderVisibility(true)
                }
                .doAfterTerminate { viewState.setLoaderVisibility(false) }
                .subscribe(
                    { routes ->
                        segment.routes = routes.toMutableList()
                        navigateToChooseSegment(segment)
                    },
                    { th ->
                        viewState.showErrorMsg(
                            getErrorDescription(th),
                            View.OnClickListener { loadOtherSegment(segment) }
                        )
                        Timber.e(th, "getSegmentRoutes failed")
                    }
                )
            )
        }
    }

    private fun navigateToChooseSegment(segment: Segment) {
        subscribeToRoutSelecting(segment)

        router.navigateTo(
            Screens.Segments(
                SegmentArgumentsWrapper(
                    searchParams = searchParams,
                    routes = segment.routes,
                    tripPlaces = currentPathGroups.tripPlaces,
                    segments = currentPathGroups.segments,
                    groupId = tripGroup.id,
                    searchUid = searchUid,
                    isReturn = segment.isReturn
                )
            )
        )
    }

    private fun subscribeToRoutSelecting(segment: Segment) {
        Injector.init(Screens.Segments.TAG)
        val segmentsUseCase = Injector.segmentsComponent?.getSegmentsUseCase() ?: return

        selectingRoutDisposable.dispose()
        selectingRoutDisposable = segmentsUseCase.getRoutSelectedObservable()
            .subscribe { route ->
                segment.selectedRoute = route
                addNewSelectedSegment(segment)
            }
    }

    private fun addNewSelectedSegment(selectedSegment: Segment) {
        selectedSegment.basketItemState = BasketItemState.SELECTED

        val nextSegmentPosition = currentPathGroups.segments.indexOf(selectedSegment) + 1
        for (i in nextSegmentPosition until currentPathGroups.segments.size) {
            val nextSegment = currentPathGroups.segments.getOrNull(i)
            nextSegment?.routes?.clear()
            nextSegment?.basketItemState = BasketItemState.NOT_SELECTED
            nextSegment?.selectedRoute = null
        }

        for (segment in currentPathGroups.segments) {
            if (segment.tripTypes.isEmpty()) {
                continue
            }

            if (segment.isOnlyTaxi()) {
                segment.basketItemState = BasketItemState.SELECTED
            }

            val basketModel = segmentsViewModels.firstOrNull { it.id == segment.id } ?: continue
            basketModel.basketItemState = segment.basketItemState

            if (segment.basketItemState == BasketItemState.SELECTED) {
                continue
            }

            basketModel.title = createNotSelectedSegmentTitle(segment.tripTypes)
        }

        val selectedBasketModel = segmentsViewModels.firstOrNull { it.id == selectedSegment.id }
        selectedBasketModel ?: return

        selectedSegment.selectedRoute?.let { selectedRoute ->
            updateSegments(selectedBasketModel, selectedRoute)
        }
        updateBuyBtn()
    }

    private fun updateBasket() {
        for (segment in currentPathGroups.segments) {
            if (segment.tripTypes.isEmpty()) {
                continue
            }

            val basketModel = segmentsViewModels.firstOrNull { it.id == segment.id } ?: continue
            basketModel.basketItemState = segment.basketItemState

            if (segment.basketItemState == BasketItemState.SELECTED) {
                val selectedBasketModel = segmentsViewModels.firstOrNull { it.id == segment.id }
                selectedBasketModel ?: continue

                segment.selectedRoute?.let { route -> updateSegments(selectedBasketModel, route) }
            } else {
                basketModel.title = createNotSelectedSegmentTitle(segment.tripTypes)
            }
        }
        updateBuyBtn()
    }

    private fun updateSegments(segmentModel: SegmentViewModel, route: Route) {
        val tripsToServices = route.tripsToServices

        var transportTitle = ""
        val objectTripType = tripsToServices.first().trip.objectType
        val tripType = objectTripType.toTripType()
        segmentModel.tripTypes = listOf(tripType)

        if (tripType.title != 0) {
            transportTitle = resources.getString(tripType.title)
        }

        val title = resources.getString(
            R.string.point_with,
            transportTitle,
            minutesToDDHHMM(route.totalDuration)
        )

        val priceAndTransferText = if (route.transferCount == 0) {
            getPrice(route).toPriceFormat()
        } else {
            resources.getString(
                R.string.point_with,
                resources.getQuantityString(
                    R.plurals.changes_count,
                    route.transferCount,
                    route.transferCount
                ),
                getPrice(route).toPriceFormat()
            )
        }

        val subDescription = resources.getString(
            R.string.basket_selected_item_description,
            route.departureDate?.formatByPattern(DATE_PATTERN),
            route.departureDate?.formatByPattern(TIME_PATTERN),
            route.arrivalDate?.formatByPattern(DATE_PATTERN),
            route.arrivalDate?.formatByPattern(TIME_PATTERN),
            priceAndTransferText
        )

        segmentModel.title = title
        segmentModel.subDescription = subDescription

        viewState.updateSegments(segmentsViewModels)
    }

    private fun updateBuyBtn() {
        val isHideBuyBtn = currentPathGroups.segments
            .any { it.basketItemState == BasketItemState.NOT_SELECTED }

        if (isHideBuyBtn) {
            viewState.hideBuyBtn()
        } else {
            setBuyBtnText()
        }
    }

    private fun createNotSelectedSegmentTitle(tripTypes: List<TripType>): String {
        var title = ""

        tripTypes.forEachIndexed { index, transportType ->
            val transport = resources.getString(transportType.title)

            title = if (tripTypes.size == 1 && transportType == TripType.TAXI) {
                return transport
            } else if (index == 0) {
                resources.getString(R.string.select_with, transport)
            } else {
                resources.getString(R.string.or_with, title, transport)
            }
        }

        return title
    }

    private fun setBuyBtnText() {
        val passengerCount = searchParams.passengers.size
        val direction = resources.getString(
            if (searchParams.arrivalDate != null) R.string.forth_and_back
            else R.string.only_forth
        )

        val primaryText = resources.getString(R.string.buy_tickets_title, getBasketPrice().toPriceFormat())
        val secondaryText = resources.getQuantityString(
            R.plurals.passenger_price,
            passengerCount,
            direction,
            passengerCount
        )

        viewState.setBuyBtn(primaryText, secondaryText.toDefaultLowerCase())
    }

    private fun getBasketPrice() : Double {
        return currentPathGroups.segments
            .mapNotNull { it.selectedRoute }
            .sumByDouble { getPrice(it) }
    }

    private fun getPrice(route: Route) : Double {
        val selectedAlternative = route.tripsToServices
            .filter { it.service.objectType.isTrain() }
            .mapNotNull { it.service.selectedAlternative }

        return if (selectedAlternative.isEmpty()) {
            route.price
        } else {
            selectedAlternative.map { it.price }.firstOrNull() ?: route.price
        }
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

    private fun reportBuyRoute(buyUrl: String) {
        analyticsManager.reportBuyRoute(
            groupId = tripGroup.id,
            price = getBasketPrice(),
            passengerNum = searchParams.passengers.size,
            segmentsCount = currentPathGroups.segments.size,
            link = buyUrl,
            totalDuration = currentPathGroups.segments.sumBy { it.selectedRoute?.totalDuration?.toInt() ?: 0 }
        )
    }
}