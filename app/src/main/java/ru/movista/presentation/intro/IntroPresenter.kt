package ru.movista.presentation.intro

import moxy.InjectViewState
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.usecase.IntroUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.intro.pager.SlidePageType
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class IntroPresenter : BasePresenter<IntroView>() {
    override val screenTag: String
        get() = Screens.Intro.TAG

    private var currentPage: Int = 0

    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var router: Router
    @Inject
    lateinit var introUseCase: IntroUseCase

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (introUseCase.isTermsAccepted()) {
            viewState.setCurrentPage(SlidePageType.SLIDE_PAGE_SIX.page)
        } else if (introUseCase.isStartingIntroSeen()) {
            viewState.setCurrentPage(SlidePageType.SLIDE_PAGE_FIVE.page)
        }

        addDisposable(introUseCase.getAllTermsSelectedSubject()
            .subscribe { onAllTermsSelected() }
        )

        addDisposable(introUseCase.getSkipClickSubject()
            .subscribe {
                goToMainScreen()
            }
        )

        addDisposable(introUseCase.getGeoAllowClickSubject()
            .subscribe { viewState.requestGeoPermissions() }
        )
    }

    override fun onPresenterInject() {
        Injector.init(Screens.Intro.TAG)
        Injector.introComponent?.inject(this)
    }

    fun nextClick() {
        viewState.setCurrentPage(currentPage + 1)
    }

    fun onPageChangeListener(page: Int) {
        currentPage = page

        if (currentPage == SlidePageType.SLIDE_PAGE_FIVE.page) {
            introUseCase.setStartingIntroSeen()
        }

        if (currentPage > SlidePageType.SLIDE_PAGE_FOUR.page) {
            viewState.hidePagerIndicator()
            viewState.hideNextButton()
            viewState.disableNextButton()
            viewState.disablePaging()
        }
    }

    fun onBackPressed() {
        if (currentPage == SlidePageType.SLIDE_PAGE_ONE.page || currentPage > SlidePageType.SLIDE_PAGE_FOUR.page) {
            router.exit()
        } else {
            viewState.setCurrentPage(currentPage - 1)
        }
    }

    fun onGrantedLocationPermissions() {
        goToMainScreen()
    }

    private fun onAllTermsSelected() {
        introUseCase.setTermsAccepted()
        viewState.setCurrentPage(currentPage + 1)
    }

    private fun goToMainScreen() {
        introUseCase.setApplicationFirstRun()
        router.replaceScreen(Screens.Chat()).also { analyticsManager.reportOnboardingFinished() }
    }

    fun onNeverRequestLocationPermissions() {
        viewState.showLaterAccessPermissionDialog()
    }

    fun onAccessPermissionClick() {
        viewState.hideAlertDialog()
        viewState.goToApplicationSettings()
    }

    fun onLaterAccessPermissionClick() {
        viewState.hideAlertDialog()
    }

    fun onDismissAlertDialog() {
        viewState.hideAlertDialog()
    }

    fun onDeniedLocationPermissions() {
    }
}