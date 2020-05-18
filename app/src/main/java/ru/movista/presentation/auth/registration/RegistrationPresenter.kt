package ru.movista.presentation.auth.registration

import moxy.InjectViewState
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.model.User
import ru.movista.domain.usecase.RegisterUserUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.custom.FlowRouter
import ru.movista.utils.schedulersIoToMain
import javax.inject.Inject

@InjectViewState
class RegistrationPresenter : BasePresenter<RegistrationView>() {

    override val screenTag: String
        get() = Screens.Registration.TAG

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var flowRouter: FlowRouter

    @Inject
    lateinit var registerUserUseCase: RegisterUserUseCase

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.registrationComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        analyticsManager.reportRegistrationStart()
    }

    fun onBackPressed(lastChild: Boolean) {
        if (lastChild) {
            flowRouter.finishFlow()
        } else {
            flowRouter.backTo(Screens.EnterPhone())
        }
    }

    fun onRegisterButtonClicked(firstName: String, lastName: String, email: String) {
        val userLastName: String? = if (lastName.isNotEmpty()) lastName else null
        addDisposable(
            registerUserUseCase.registerUser(User(firstName, userLastName, email))
                .schedulersIoToMain()
                .doOnSubscribe { viewState.showLoading() }
                .doFinally { viewState.hideLoading() }
                .subscribe(
                    {
                        registerUserUseCase.setUserRegistered()
                        flowRouter.finishFlow()
                        analyticsManager.reportRegistrationSuccess()
                    },
                    {
                        viewState.showError(getErrorDescription(it))
                    }
                )
        )
    }
}