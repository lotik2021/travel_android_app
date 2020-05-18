package ru.movista.presentation.tickets.segments

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.data.source.local.models.TicketSortingOption
import ru.movista.domain.model.tickets.Route

@StateStrategyType(AddToEndSingleStrategy::class)
interface SegmentsView : MvpView {
    fun setTitle(description: String)
    fun updateTickets(routes: List<Route>)
    fun setEnabledFilterIndicator(enabled: Boolean)
    fun setFiltersVisibility(visible: Boolean)
    fun showEmptyRoutesMessage()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTicketsExpiredTimer()

    @StateStrategyType(SkipStrategy::class)
    fun openSortingOptions(ticketSortingOption: TicketSortingOption)
}