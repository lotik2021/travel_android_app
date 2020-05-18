package ru.movista.presentation.intro.pager

import moxy.InjectViewState
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.usecase.SlidePageUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SlidePagePresenter : BasePresenter<SlidePageView>() {

    override val screenTag: String
        get() = Screens.Intro.TAG

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var introUseCase: SlidePageUseCase

    override fun onPresenterInject() {
        Injector.init(Screens.Intro.TAG)
        Injector.introComponent?.inject(this)
    }

    override fun onDestroy() {
        Timber.d("Lifecycle callback: ${this.javaClass.simpleName} onDestroy")
    }

    fun onAllTermsSelected() {
        introUseCase.onAllTermsSelected().also { analyticsManager.reportLegalAccepted() }
    }

    fun onSkipClick() {
        introUseCase.onSkipClick().also { analyticsManager.reportOnboardingDisallowedLocation() }
    }

    fun onGeoAllowClick() {
        introUseCase.onGeoAllowClick().also { analyticsManager.reportOnboardingAllowedLocation() }
    }
}