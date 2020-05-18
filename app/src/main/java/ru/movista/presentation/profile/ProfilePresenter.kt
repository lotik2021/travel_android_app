package ru.movista.presentation.profile

import android.content.res.Resources
import android.os.Build
import moxy.InjectViewState
import ru.movista.BuildConfig
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.model.UserProfile
import ru.movista.domain.usecase.ProfileUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.utils.schedulersIoToMain
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ProfilePresenter : BasePresenter<ProfileView>() {
    override val screenTag: String
        get() = Screens.Profile.TAG

    private lateinit var userProfile: UserProfile

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var profileUseCase: ProfileUseCase

    override fun onPresenterInject() {
        Injector.init(Screens.Profile.TAG)
        Injector.profileComponent?.inject(this)
    }

    override fun attachView(view: ProfileView) {
        super.attachView(view)
        if (profileUseCase.isUserRegistered()) {
            viewState.setProfileContent()
            viewState.setUserName(profileUseCase.getUserFullName())
            loadProfile()
        } else {
            viewState.setUnAuthContent()
            viewState.setEditProfileBtnVisibility(false)
            viewState.setRoutesContentVisibility(false)
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onFeedbackClick() {
        viewState.navigateToFeedBackMailApplication(
            BuildConfig.REPORT_EMAIL_ADDRESS,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            Build.BRAND,
            Build.MODEL,
            Build.VERSION.RELEASE
        )
    }

    fun onLegalDocumentsClick() {
        router.navigateTo(Screens.LegalDocuments())
    }

    fun onAboutAppClick() {
        router.navigateTo(Screens.AboutApp())
    }

    fun onSignInClicked() {
        router.navigateTo(Screens.AuthFlow()).also {
            analyticsManager.reportLoginStart()
        }
    }

    fun onEditProfileClick() {
        router.navigateTo(Screens.EditProfile())
    }

    fun onTransportTypesClick() {
        router.navigateTo(Screens.TransportTypes(userProfile.googleTransports))
    }

    fun onFavoriteAddressesClick() {
        analyticsManager.reportOpenFavorites()
        router.navigateTo(Screens.Favorites(userProfile.userFavoritesPlaces))
    }

    fun onSnackBarActionClick() {
        loadProfile()
        viewState.hideSnackBar()
    }

    private fun loadProfile() {
        addDisposable(
            profileUseCase.loadProfile()
                .schedulersIoToMain()
                .doOnSubscribe {
                    viewState.setInfoContentVisibility(false)
                    viewState.setRoutesContentVisibility(false)
                    viewState.setEditProfileBtnVisibility(false)
                    viewState.setLoaderVisibility(true)
                }
                .doAfterTerminate {
                    viewState.setInfoContentVisibility(true)
                    viewState.setLoaderVisibility(false)
                }
                .subscribe(
                    { profile ->
                        userProfile = profile
                        viewState.setUserName("${profile.name} ${profile.lastName}")
                        viewState.setRoutesContentVisibility(true)
                        viewState.setEditProfileBtnVisibility(false)
                    },
                    { th ->
                        Timber.e("loadProfile onError, ${th.message}")
                        viewState.showMsg(resources.getString(R.string.connection_error))
                    }
                )
        )
    }
}