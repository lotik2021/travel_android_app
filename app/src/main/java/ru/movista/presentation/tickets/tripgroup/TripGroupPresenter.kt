package ru.movista.presentation.tickets.tripgroup

import android.content.res.Resources
import android.view.View
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import moxy.InjectViewState
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.model.tickets.TripGroup
import ru.movista.domain.usecase.tickets.BasketUseCase
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.domain.usecase.tickets.TripGroupUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.tickets.segments.SegmentArgumentsWrapper
import ru.movista.utils.formatByPattern
import ru.movista.utils.schedulersIoToMain
import ru.movista.utils.toDefaultLowerCase
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class TripGroupPresenter(
    private val searchParams: SearchModel
) : BasePresenter<TripGroupView>() {

    override val screenTag: String
        get() = Screens.TripGroupScreen.TAG

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var ticketSearchUseCase: TripGroupUseCase
    @Inject
    lateinit var ticketsUseCase: TicketsUseCase
    @Inject
    lateinit var resources: Resources
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var basketUseCase: BasketUseCase

    private var ticketSearchDisposable = Disposables.disposed()
    private val datePattern = "dd LLL, EE"
    private var currentSearchUid: String? = null
    private var ticketsLifeTImeTimer: Disposable? = null
    private val tripGroupsResult: MutableList<TripGroup> = mutableListOf()
    private var isRoundTrip = false

    val isShowSearchHint: Boolean

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.tripGroupComponent?.inject(this)
    }

    init {
        isShowSearchHint = ticketSearchUseCase.isShowSearchHint()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        with(viewState) {
            setComfortType(resources.getString(searchParams.comfortType.resId).toDefaultLowerCase())
            setTripPlace(searchParams.fromPlace?.name, searchParams.toPlace?.name)

            searchParams.departureDate?.let { departureDate ->
                val toDate = departureDate.formatByPattern(datePattern)
                var returnDate: String? = null

                searchParams.arrivalDate?.let {
                    returnDate = it.formatByPattern(datePattern)
                    isRoundTrip = true
                }

                setDate(toDate, returnDate)
            }
        }

        startSearch()
    }

    private fun startSearch() {
        ticketSearchDisposable.dispose()
        ticketSearchDisposable = ticketSearchUseCase.asyncTicketsSearch(searchParams)
            .schedulersIoToMain()
            .doOnSubscribe {
                viewState.hideErrorMsg()
                viewState.setLoaderVisibility(true)
            }
            .doAfterTerminate { viewState.setLoaderVisibility(false) }
            .subscribe(
                { result ->
                    val isCompletedSearch: Boolean = result.second
                    currentSearchUid = result.first.uid
                    tripGroupsResult.clear()
                    tripGroupsResult.addAll(result.first.searchResult)

                    viewState.updateSearchResultItems(tripGroupsResult, isCompletedSearch)

                    if (isCompletedSearch && tripGroupsResult.isNullOrEmpty()) {
                        viewState.showEmptyRoutesMessage()
                    }

                    subscribeToTicketsLifeTImeTimer()
                },
                { th ->
                    viewState.updateSearchResultItems(tripGroupsResult, completedSearch = true)
                    viewState.showErrorMsg(
                        getErrorDescription(th),
                        View.OnClickListener { onRepeatSearchClick() }
                    )
                    Timber.e(th, "AsyncTicketsSearch failed")
                }
            )
    }

    fun onTicketClick(tripGroup: TripGroup) {
        currentSearchUid?.let {
            loadSegments(it, tripGroup)

            analyticsManager.reportGroupOfRoutesClick(
                groupId = tripGroup.id,
                groupsCount = tripGroupsResult.size,
                priceSelected = tripGroup.minPrice ?: 0.0,
                segmentsCount = tripGroup.tripTypeSequenceIcons.size
            )
        }
    }

    fun onBackClicked() {
        router.exit()
        analyticsManager.reportBackwardsClick("route_groups")
    }

    fun onNewSearchClick() {
        router.exit()
    }

    fun onNotShowSearchHintClick() {
        ticketSearchUseCase.saveDisablingSearchHint()
        viewState.removeSearchHint()
    }

    fun onRepeatExpiredSearchClick() {
        tripGroupsResult.clear()
        viewState.restartSearchResultUi()
        startSearch()
    }

    fun onCancelSearchClick() {
        router.backTo(Screens.SearchTickets())
    }

    override fun onDestroy() {
        ticketSearchDisposable.dispose()
        ticketsLifeTImeTimer?.dispose()
        super.onDestroy()
    }

    private fun onRepeatSearchClick() {
        viewState.restartSearchResultUi()
        startSearch()
    }

    private fun subscribeToTicketsLifeTImeTimer() {
        if (ticketsLifeTImeTimer == null) {
            ticketsLifeTImeTimer = ticketsUseCase.startTicketsLifetimeTimer()
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

    private fun loadSegments(searchUid: String, tripGroup: TripGroup) {
        addDisposable(basketUseCase.getPathGroup(searchUid, tripGroup.id)
            .schedulersIoToMain()
            .doOnSubscribe {
                viewState.hideErrorMsg()
                viewState.setOnlyLoaderVisibility(true)
            }
            .doAfterTerminate { viewState.setOnlyLoaderVisibility(false) }
            .subscribe(
                { pathGroups ->
                    if (!isRoundTrip && tripGroup.isSingleSegment()) {
                        router.navigateTo(
                            Screens.Segments(
                                SegmentArgumentsWrapper(
                                    searchParams = searchParams,
                                    routes = pathGroups.segments.first().routes,
                                    tripPlaces = pathGroups.tripPlaces,
                                    isReturn = pathGroups.segments.first().isReturn,
                                    segments = pathGroups.segments,
                                    groupId = tripGroup.id,
                                    searchUid = searchUid,
                                    isSkipBasket = true
                                )
                            )
                        )
                    } else {
                        router.navigateTo(Screens.Basket(searchParams, searchUid, tripGroup, pathGroups))
                    }
                },
                { th ->
                    viewState.showErrorMsg(
                        getErrorDescription(th),
                        View.OnClickListener { loadSegments(searchUid, tripGroup) }
                    )
                    Timber.e(th, "getPathGroup failed")
                }
            )
        )
    }

    private fun TripGroup.isSingleSegment() : Boolean {
        return this.tripTypeSequenceIcons.size == 1 && this.tripTypeSequenceIcons.first().size == 1
    }
}