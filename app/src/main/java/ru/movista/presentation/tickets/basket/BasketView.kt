package ru.movista.presentation.tickets.basket

import android.view.View
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.data.source.local.models.TripType
import ru.movista.presentation.viewmodel.SegmentViewModel

@StateStrategyType(AddToEndSingleStrategy::class)
interface BasketView : MvpView {
    fun setLoaderVisibility(visible: Boolean)
    fun setBuyBtnVisibility(visible: Boolean)
    fun setRoutsIcons(items: List<List<TripType>>)
    fun updateSegments(items: List<SegmentViewModel>)
    fun setDate(forthDateTitle: String, backDateTitle: String?)
    fun setBuyBtn(primaryText: String, secondaryText: String)
    fun hideBuyBtn()

    @StateStrategyType(SkipStrategy::class)
    fun openSegmentOptions(segmentViewModel: SegmentViewModel)

    @StateStrategyType(SkipStrategy::class)
    fun removeBasketHint()

    @StateStrategyType(SkipStrategy::class)
    fun openExternalUrl(url: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTicketsExpiredTimer()

    @StateStrategyType(SkipStrategy::class)
    fun showSimpleErrorMsg(message: String)

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMsg(message: String, action: View.OnClickListener)

    @StateStrategyType(SkipStrategy::class)
    fun hideErrorMsg()
}