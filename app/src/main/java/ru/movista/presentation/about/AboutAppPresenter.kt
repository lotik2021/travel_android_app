package ru.movista.presentation.about

import moxy.InjectViewState
import ru.movista.BuildConfig
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.data.source.local.GOOGLE_PLAY_APP_LINK
import ru.movista.data.source.local.SocialNetwork
import ru.terrakok.cicerone.Router

@InjectViewState
class AboutAppPresenter : BasePresenter<AboutAppView>() {
    override val screenTag: String
        get() = Screens.AboutApp.TAG

    private var router: Router? = null

    override fun onPresenterInject() {
        router = Injector.mainComponent?.getRouter()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppVersion("${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
    }

    fun onBackClicked() {
        router?.exit()
    }

    fun onVkClick() {
        viewState.openInCustomBrowser(SocialNetwork.VK.url)
    }

    fun onGooglePlayReviewsClick() {
        viewState.openInCustomBrowser(GOOGLE_PLAY_APP_LINK)
    }

    fun onInstagramClick() {
        viewState.openInCustomBrowser(SocialNetwork.INSTAGRAM.url)
    }

    fun onOkClick() {
        viewState.openInCustomBrowser(SocialNetwork.OK.url)
    }

    fun onFacebookClick() {
        viewState.openInCustomBrowser(SocialNetwork.FACEBOOK.url)
    }

    fun onYoutubeClick() {
        viewState.openInCustomBrowser(SocialNetwork.YOUTUBE.url)
    }

    fun onTwitterClick() {
        viewState.openInCustomBrowser(SocialNetwork.TWITTER.url)
    }
}