package ru.movista.presentation.profile.transporttypes

import moxy.InjectViewState
import ru.movista.di.Injector
import ru.movista.domain.model.ProfileTransportType
import ru.movista.domain.usecase.ProfileUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.utils.schedulersIoToMain
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

@InjectViewState
class TransportTypesPresenter(profileTransportTypes: Serializable?) : BasePresenter<TransportTypesView>() {
    override val screenTag: String
        get() = Screens.TransportTypes.TAG

    private lateinit var profileTransportTypes: List<ProfileTransportType>

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var profileUseCase: ProfileUseCase

    init {
        if (profileTransportTypes == null || profileTransportTypes !is List<*>) {
            Timber.e("Expected RouteViewModel but was $profileTransportTypes")
        } else {
            this.profileTransportTypes = profileTransportTypes as List<ProfileTransportType>
        }
    }

    override fun onPresenterInject() {
        Injector.init(Screens.Profile.TAG)
        Injector.profileComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setTransportTypes(profileTransportTypes)
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onTransportCheckedChange(item: ProfileTransportType, checked: Boolean) {
        profileTransportTypes.find { it.id == item.id }?.isOn = checked
    }

    fun onSaveClick() {
        addDisposable(
            profileUseCase.updateUserTransportTypes(profileTransportTypes)
                .schedulersIoToMain()
                .doOnSubscribe { viewState.showLoading() }
                .doFinally { viewState.hideLoading() }
                .subscribe(
                    { router.exit() },
                    { viewState.showError(getErrorDescription(it)) }
                )
        )
    }
}