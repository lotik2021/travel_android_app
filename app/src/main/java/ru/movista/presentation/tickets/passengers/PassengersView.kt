package ru.movista.presentation.tickets.passengers

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface PassengersView : MvpView {

    fun showPassengersInfo(passengersInfoViewModel: PassengersInfoViewModel)

    fun disableIncreaseAdultCountIf(condition: Boolean)
    fun disableIncreaseChildrenCountIf(condition: Boolean)
    fun disableDecreaseAdultCountIf(condition: Boolean)
    fun disableDecreaseChildrenCountIf(condition: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showChildInfoDialog(childInfoViewModel: ChildInfoViewModel?)

    @StateStrategyType(SkipStrategy::class)
    fun showMsg(@StringRes messageResId: Int)
}