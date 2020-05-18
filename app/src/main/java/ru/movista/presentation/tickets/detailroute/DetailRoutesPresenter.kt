package ru.movista.presentation.tickets.detailroute

import android.content.res.Resources
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.source.local.models.ObjectTripType
import ru.movista.data.source.local.models.isNotTransfer
import ru.movista.data.source.local.models.isTrain
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.Alternative
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.usecase.tickets.BasketUseCase
import ru.movista.domain.usecase.tickets.SegmentsUseCase
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.tickets.segments.SegmentArgumentsWrapper
import ru.movista.utils.schedulersIoToMain
import ru.movista.utils.toDefaultLowerCase
import ru.movista.utils.toPriceFormat
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class DetailRoutesPresenter(
    private val segmentArguments: SegmentArgumentsWrapper,
    private val detailRoutesMode: DetailRoutesMode
) : BasePresenter<DetailRouteView>() {
    override val screenTag: String
        get() = Screens.DetailRoutes.TAG

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
    @Inject
    lateinit var basketUseCase: BasketUseCase

    private var ticketsLifeTImeTimer: Disposable? = null

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.segmentsComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (detailRoutesMode == DetailRoutesMode.SHOVING) {
            viewState.setToolbarTitle(R.string.more_detail)
        } else if (detailRoutesMode == DetailRoutesMode.SELECTING) {
            viewState.setToolbarTitle(R.string.detail_routes)
        }

        segmentArguments.route?.let { route ->
            viewState.updateItems(route, segmentArguments.tripPlaces)
            setApplyBtnText(route)
        }

        subscribeToTicketsLifeTImeTimer()
    }

    fun onBackClicked() {
        router.exit()
        analyticsManager.reportBackwardsClick("ticket_details")
    }

    fun onSelectedAlternative(alternative: Alternative) {
        segmentArguments.route?.let { route ->
            val tripsToService = route.tripsToServices

            if (tripsToService.isNullOrEmpty()) {
                return
            }

            val firstTripsToService = tripsToService.firstOrNull()
            if (firstTripsToService?.trip?.objectType != ObjectTripType.TRAIN) {
                return
            }

            val selectedCoachType = firstTripsToService.service.selectedAlternative?.coachType
            if (detailRoutesMode == DetailRoutesMode.SHOVING
                && selectedCoachType != null
                && selectedCoachType != alternative.coachType) {
                viewState.showMsg(R.string.coach_type_saved_description)
            }

            tripsToService.forEach {
                if (firstTripsToService.trip.objectType == ObjectTripType.TRAIN) {
                    it.service.selectedAlternative = alternative
                }
            }

            setApplyBtnText(route)
        }
    }

    fun onPriceInfoClick() {
        viewState.showPriceInfoAlert(resources.getString(R.string.ticket_price_description))
    }

    fun onDismissAlertDialog() {
        viewState.hideAlertDialog()
    }

    fun onOkPriceInfoClick() {
        viewState.hideAlertDialog()
    }

    fun onRepeatExpiredSearchClick() {
        router.backTo(Screens.SearchTickets())
        router.navigateTo(Screens.TripGroupScreen(segmentArguments.searchParams))
    }

    fun onCancelSearchClick() {
        router.backTo(Screens.SearchTickets())
    }

    private fun setApplyBtnText(route: Route) {
        if (detailRoutesMode == DetailRoutesMode.SHOVING) {
            viewState.setSimpleApplyBtn(resources.getString(R.string.change_selection))
            return
        }

        val minPrice = getPrice(route)
        val passengerCount = segmentArguments.searchParams.passengers.size
        val direction = resources.getString(
            if (segmentArguments.isReturn) R.string.only_back
            else R.string.only_forth
        )

        var primaryText = ""

        if (detailRoutesMode == DetailRoutesMode.BUYING) {
            primaryText = resources.getString(R.string.buy_tickets_title, minPrice.toPriceFormat())
        } else if (detailRoutesMode == DetailRoutesMode.SELECTING) {
            primaryText = resources.getString(R.string.select_route_title, minPrice.toPriceFormat())
        }

        val secondaryText = resources.getQuantityString(
            R.plurals.passenger_price,
            passengerCount,
            direction,
            passengerCount
        )

        viewState.setApplyBtn(primaryText, secondaryText.toDefaultLowerCase())
    }

    private fun getPrice(route: Route): Double {
        val selectedAlternative = route.tripsToServices
            .filter { it.service.objectType.isTrain() }
            .mapNotNull { it.service.selectedAlternative }

        return if (selectedAlternative.isEmpty()) {
            route.price
        } else {
            selectedAlternative.map { it.price }.firstOrNull() ?: route.price
        }
    }

    fun onApplyClick() {
        when (detailRoutesMode) {
            DetailRoutesMode.SHOVING -> {
                router.navigateTo(Screens.Segments(segmentArguments))
                analyticsManager.reportChangeRoute()
            }
            DetailRoutesMode.SELECTING -> {
                segmentArguments.route?.let {
                    segmentsUseCase.sendRoutSelected(it)
                    router.backTo(Screens.Basket())
                    segmentArguments.route?.let { route -> reportChooseTickets(route) }
                }
            }
            DetailRoutesMode.BUYING -> saveSelectedRoutes()
        }
    }

    private fun saveSelectedRoutes() {
        segmentArguments.segments.first().selectedRoute = segmentArguments.route

        addDisposable(basketUseCase.saveSelectedRoutes(
            segmentArguments.searchUid,
            segmentArguments.groupId,
            segmentArguments.segments
        )
            .schedulersIoToMain()
            .doOnSubscribe { viewState.setLoaderVisibility(true) }
            .doAfterTerminate { viewState.setLoaderVisibility(false) }
            .subscribe(
                { response ->
                    response.actualUrl?.let { url ->
                        viewState.openExternalUrl(url)
                        reportBuyRoute(url)
                    }
                },
                { th ->
                    viewState.showMsg(getErrorDescription(th))
                    Timber.e(th, "saveSelectedRoutes failed")
                }
            )
        )
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

    private fun reportChooseTickets(route: Route) {
        val firstTripsToServices = route.tripsToServices.firstOrNull { it.trip.objectType.isNotTransfer() }
        firstTripsToServices?.let {
            analyticsManager.reportChooseTickets(
                tripType = firstTripsToServices.trip.tripType.id,
                price = route.price
            )
        }
    }

    private fun reportBuyRoute(buyUrl: String) {
        analyticsManager.reportBuyRoute(
            groupId = segmentArguments.groupId,
            price = segmentArguments.route?.let { getPrice(it) } ?: 0.0,
            passengerNum = segmentArguments.searchParams.passengers.size,
            segmentsCount = segmentArguments.segments.size,
            link = buyUrl,
            totalDuration = segmentArguments.segments.sumBy { it.selectedRoute?.totalDuration?.toInt() ?: 0 }
        )
    }
}