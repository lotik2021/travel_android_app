package ru.movista.presentation.tickets.searchtickets

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SearchTicketsView : MvpView {

    fun setFromPlace(name: String, lookInactive: Boolean)
    fun setToPlace(name: String, lookInactive: Boolean)

    fun setFromDate(date: String, lookInactive: Boolean)
    fun setToDate(date: String, lookInactive: Boolean)

    fun setPassengersInfo(adultsCount: String, childrenCount: String)

    @StateStrategyType(SkipStrategy::class)
    fun showSelectComfortTypeDialog(comfortTypeValue: String)

    fun setComfortType(comfortType: String)

    @StateStrategyType(SkipStrategy::class)
    fun showError(@StringRes errorMsgRes: Int)
}