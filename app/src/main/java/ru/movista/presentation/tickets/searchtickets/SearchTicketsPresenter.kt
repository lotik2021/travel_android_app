package ru.movista.presentation.tickets.searchtickets

import android.content.res.Resources
import moxy.InjectViewState
import org.threeten.bp.format.DateTimeFormatter
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.AdultPassenger
import ru.movista.domain.model.tickets.ChildPassenger
import ru.movista.domain.model.tickets.ComfortType
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.usecase.tickets.searchparams.IComfortTypeUseCase
import ru.movista.domain.usecase.tickets.searchparams.ISearchTicketsUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.tickets.placeselect.PlaceSelectType
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SearchTicketsPresenter : BasePresenter<SearchTicketsView>() {

    companion object {
        private const val DATE_FORMAT = "d MMM, EEE"
    }

    override val screenTag: String
        get() = Screens.SearchTickets.TAG

    private lateinit var searchParams: SearchModel

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var searchUseCase: ISearchTicketsUseCase

    @Inject
    lateinit var comfortTypeUseCase: IComfortTypeUseCase

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.searchTicketsComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        searchParams = searchUseCase.getSearchParams()
        updateView()

        analyticsManager.reportOpenSearchRoutes()
    }

    override fun attachView(view: SearchTicketsView) {
        super.attachView(view)
        Timber.i("Updated searchPlaces params: $searchParams")
        updateView()
    }

    fun onFromClicked() {
        router.navigateTo(Screens.PlaceSelect(PlaceSelectType.FROM.name))
    }

    fun onToClicked() {
        router.navigateTo(Screens.PlaceSelect(PlaceSelectType.TO.name))
    }

    fun onFromDateClicked() {
        router.navigateTo(Screens.DateSelect())
    }

    fun onToDateClicked() {
        router.navigateTo(Screens.DateSelect())
    }

    fun onPassengersClicked() {
        router.navigateTo(Screens.Passengers())
    }

    fun onComfortTypeClicked() {
        viewState.showSelectComfortTypeDialog(searchParams.comfortType.name)
    }

    fun onComfortTypeSelected(comfortType: ComfortType) {
        comfortTypeUseCase.setComfortType(comfortType)
        updateComfortType()
    }

    fun onSwapPlacesClicked() {
        searchUseCase.swapPlaces()
        updatePlaces()
    }

    fun onSearchClicked() {
        with(searchParams) {
            when {
                fromPlace == null -> viewState.showError(R.string.error_search_params_from_place)
                toPlace == null -> viewState.showError(R.string.error_search_params_to_place)
                departureDate == null -> viewState.showError(R.string.error_search_params_from_date)
                toPlace?.id == fromPlace?.id -> viewState.showError(R.string.routs_direction_error)
                else -> {
                    router.navigateTo(Screens.TripGroupScreen(searchParams))

                    analyticsManager.reportSearchStartRoutes(
                        fromId = fromPlace?.id ?: 0,
                        toId = toPlace?.id ?: 0,
                        owert = if (arrivalDate == null) "oneway" else "roundtrip",
                        departureDate = departureDate.toString(),
                        returnDepartureDate = arrivalDate.toString(),
                        passengerNum = passengers.size
                    )
                }
            }
        }
    }

    fun onChatScreenClick() {
        router.exit()
        analyticsManager.reportOpenChat()
    }

    fun onBackClicked() {
        router.exit()
        analyticsManager.reportBackwardsClick("search_main")
    }

    private fun updateView() {
        updatePlaces()
        updateDates()
        updatePassengers()
        updateComfortType()
    }

    private fun updatePlaces() {
        updateFromPlace()
        updateToPlace()
    }

    private fun updateFromPlace() {
        searchParams.fromPlace?.let {
            viewState.setFromPlace(
                it.name,
                false
            )
        } ?: viewState.setFromPlace(
            resources.getString(R.string.unfilled_place_placeholder),
            true
        )
    }

    private fun updateToPlace() {
        searchParams.toPlace?.let {
            viewState.setToPlace(
                it.name,
                false
            )
        } ?: viewState.setToPlace(
            resources.getString(R.string.unfilled_place_placeholder),
            true
        )
    }

    private fun updateDates() {
        val fromDateText = searchParams.departureDate?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
            ?: resources.getString(R.string.date).toLowerCase()

        val toDayText = searchParams.arrivalDate?.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
            ?: resources.getString(R.string.date).toLowerCase()

        viewState.setFromDate(fromDateText, searchParams.departureDate == null)
        viewState.setToDate(toDayText, searchParams.arrivalDate == null)
    }

    private fun updatePassengers() {
        val adultsCount = searchParams.passengers.filterIsInstance<AdultPassenger>().size.toString()
        val childrenCount =
            searchParams.passengers.filterIsInstance<ChildPassenger>().size.toString()

        viewState.setPassengersInfo(adultsCount, childrenCount)
    }

    private fun updateComfortType() {
        val comfortTypeName = when (searchParams.comfortType) {
            ComfortType.ECONOMY -> resources.getString(R.string.comfort_type_economy)
            ComfortType.PREMIUM_ECONOMY -> resources.getString(R.string.comfort_type_premium_economy)
            ComfortType.BUSINESS -> resources.getString(R.string.comfort_type_business)
            ComfortType.FIRST_CLASS -> resources.getString(R.string.comfort_type_first_class)
        }
        viewState.setComfortType(comfortTypeName.toLowerCase())
    }
}