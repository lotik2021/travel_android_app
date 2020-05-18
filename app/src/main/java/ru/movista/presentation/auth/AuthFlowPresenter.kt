package ru.movista.presentation.auth

import moxy.InjectViewState
import ru.movista.di.Injector
import ru.movista.domain.usecase.IAuthUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.custom.FlowRouter
import javax.inject.Inject

@InjectViewState
class AuthFlowPresenter : BasePresenter<AuthFlowView>() {

    override val screenTag: String
        get() = Screens.AuthFlow.TAG

    @Inject
    lateinit var router: FlowRouter

    @Inject
    lateinit var authUseCase: IAuthUseCase

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.authFlowComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        router.replaceScreen(Screens.EnterPhone())
    }

    fun onBackPressed() {
        router.finishFlow()
    }
}