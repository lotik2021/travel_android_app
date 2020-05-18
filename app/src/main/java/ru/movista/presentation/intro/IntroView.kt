package ru.movista.presentation.intro

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface IntroView : MvpView {
    fun setCurrentPage(page: Int)
    fun hidePagerIndicator()
    fun hideNextButton()
    fun disablePaging()
    fun showLaterAccessPermissionDialog()
    fun hideAlertDialog()
    fun goToApplicationSettings()
    fun disableNextButton()

    @StateStrategyType(SkipStrategy::class)
    fun requestGeoPermissions()
}