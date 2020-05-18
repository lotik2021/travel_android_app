package ru.movista.presentation.auth.enterphone

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.presentation.base.BaseLoadingView

@StateStrategyType(AddToEndSingleStrategy::class)
interface EnterPhoneView : BaseLoadingView {
    fun setEnterPhoneNextButtonEnabled(enabled: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun verifyWithRecaptcha(siteKey: String)
}