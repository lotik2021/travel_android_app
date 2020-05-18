package ru.movista.presentation.tickets.tripgroup

import android.view.View
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.domain.model.tickets.TripGroup

@StateStrategyType(AddToEndSingleStrategy::class)
interface TripGroupView : MvpView {
    fun setLoaderVisibility(visible: Boolean)
    fun updateSearchResultItems(result: List<TripGroup>, completedSearch: Boolean)
    fun restartSearchResultUi()
    fun setTripPlace(fromPlace: String?, toPlace: String?)
    fun setDate(toDate: String, returnDate: String?)
    fun setComfortType(comfortType: String)
    fun showEmptyRoutesMessage()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMsg(message: String, action: View.OnClickListener)

    @StateStrategyType(SkipStrategy::class)
    fun hideErrorMsg()

    @StateStrategyType(SkipStrategy::class)
    fun removeSearchHint()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTicketsExpiredTimer()

    @StateStrategyType(SkipStrategy::class)
    fun setOnlyLoaderVisibility(visible: Boolean)
}