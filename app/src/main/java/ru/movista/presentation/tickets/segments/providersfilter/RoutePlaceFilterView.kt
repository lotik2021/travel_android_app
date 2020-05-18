package ru.movista.presentation.tickets.segments.providersfilter

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface RoutePlaceFilterView : MvpView {
    fun setTitle(titleResId: Int)
    fun updateFilter(filtersItem: List<BaseFilterViewModel>)
    fun setApplyBtnVisibility(visible: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTicketsExpiredTimer()
}