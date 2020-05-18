package ru.movista.presentation.base

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface BaseLoadingView : MvpView {

    fun showLoading()
    fun hideLoading()

    @StateStrategyType(SkipStrategy::class)
    fun showError(error: String)
}