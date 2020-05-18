package ru.movista.presentation.about

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface AboutAppView : MvpView {
    fun setAppVersion(version: String)

    @StateStrategyType(SkipStrategy::class)
    fun openInCustomBrowser(url: String)
}