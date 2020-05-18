package ru.movista.presentation.tickets.segments.segmentfilter

import androidx.annotation.ColorRes
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface SegmentFilterView : MvpView {
    fun setDepartureRangeBarIndex(minTime: Long, maxTime: Long)
    fun setArrivalRangeBarIndex(minTime: Long, maxTime: Long)
    fun setMaxTripDurationRangeBarIndex(maxTime: Long)
    fun setPriceRangeBarIndex(minPrice: Int, maxPrice: Int)
    fun setMaxTransferCountRangeBarIndex(maxCount: Int)
    fun setMaxTripDurationLabel(text: String)
    fun setMaxTripDurationLabelColor(@ColorRes colorResId: Int)
    fun setApplyBtnText(secondaryText: String)
    fun setFiltersCarriersLabel(text: String?)
    fun setFiltersDeparturePointsLabel(text: String?)
    fun setFiltersArrivalPointsLabel(text: String?)
    fun setupMaxTripDurationRangeBar(min: Long, max: Long)
    fun setTripDurationLabels(min: String, max: String)
    fun setupPriceRangeBar(min: Int, max: Int)
    fun setupMaxTransferCountRangeBar(maxTransferCount: Int)
    fun setMaxTransferCountLabel(text: String)
    fun setupDepartureRangeBar(min: Long, max: Long)
    fun setupArrivalRangeBar(min: Long, max: Long)
    fun disableMaxTripDurationRangeBar()
    fun disablePriceRangeBar()
    fun disableMaxTransferCountRangeBar()
    fun setResetTimeBtnVisibility(visible: Boolean)
    fun setResetRouteBtnVisibility(visible: Boolean)
    fun setResetPointsBtnVisibility(visible: Boolean)
    fun setAllResetBtnsVisibility(visible: Boolean)
    fun setApplyBtnEnabled(enabled: Boolean)
    fun enableRangeBarsListeners()
    fun setCarriersLabelText(text: String)
    fun setCarriersLabelEnabled(enabled: Boolean)
    fun setDeparturePointsLabelText(text: String)
    fun setDeparturePointsLabelEnabled(enabled: Boolean)
    fun setArrivalPointsLabelText(text: String)
    fun setArrivalPointsLabelEnabled(enabled: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTicketsExpiredTimer()
}