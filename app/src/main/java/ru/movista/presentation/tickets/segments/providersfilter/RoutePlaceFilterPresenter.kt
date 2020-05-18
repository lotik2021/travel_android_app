package ru.movista.presentation.tickets.segments.providersfilter

import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import ru.movista.data.source.local.models.RouteFilterType
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.usecase.tickets.SegmentsUseCase
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.tickets.segments.RoutePlaceFilter
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class RoutePlaceFilterPresenter(
    private val routeFilterType: RouteFilterType,
    private val oldRoutePlaceFilters: List<RoutePlaceFilter>,
    private val searchParams: SearchModel
) : BasePresenter<RoutePlaceFilterView>() {
    override val screenTag: String
        get() = Screens.SelectableDataFilter.TAG

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var segmentsUseCase: SegmentsUseCase
    @Inject
    lateinit var ticketsUseCase: TicketsUseCase

    private var ticketsLifeTImeTimer: Disposable? = null
    private val filterItemsViewModel = mutableListOf<BaseFilterViewModel>()
    private val routePlaceFilters: List<RoutePlaceFilter> = oldRoutePlaceFilters.map { it.copy() }

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.segmentsComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setTitle(routeFilterType.titleResId)

        routePlaceFilters.forEach { filter ->
            filterItemsViewModel.add(TitleFilterViewModel(filter.title, filter.tripType, filter.routeItems.all { it.isSelected }))
            filterItemsViewModel.addAll(filter.routeItems.map { ItemFilterViewModel(it, filter.tripType) })
        }

        viewState.updateFilter(filterItemsViewModel)
        subscribeToTicketsLifeTImeTimer()
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onFilterItemChanged() {
        viewState.setApplyBtnVisibility(routePlaceFilters != oldRoutePlaceFilters)
    }

    fun onApplyClick() {
        segmentsUseCase.applyRoutePlaceFilter(routeFilterType, routePlaceFilters)
        router.exit()
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
}