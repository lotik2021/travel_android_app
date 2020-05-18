package ru.movista.presentation.documents

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface LegalDocumentsView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun openInCustomBrowser(url: String)
}