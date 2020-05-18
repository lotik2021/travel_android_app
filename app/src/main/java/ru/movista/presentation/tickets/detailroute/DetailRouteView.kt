package ru.movista.presentation.tickets.detailroute

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.TripPlace

@StateStrategyType(AddToEndSingleStrategy::class)
interface DetailRouteView : MvpView {
    fun setApplyBtn(primaryText: String, secondaryText: String)
    fun updateItems(route: Route, tripPlaces: Map<Long, TripPlace>)
    fun hideAlertDialog()
    fun showPriceInfoAlert(description: String)
    fun setSimpleApplyBtn(text: String)
    fun setToolbarTitle(titleResId: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTicketsExpiredTimer()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMsg(@StringRes messageResId: Int)

    @StateStrategyType(SkipStrategy::class)
    fun setLoaderVisibility(visible: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun openExternalUrl(url: String)

    @StateStrategyType(SkipStrategy::class)
    fun showMsg(message: String)
}