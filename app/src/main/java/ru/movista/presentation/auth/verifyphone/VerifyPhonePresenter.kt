package ru.movista.presentation.auth.verifyphone

import android.content.res.Resources
import io.reactivex.android.schedulers.AndroidSchedulers
import moxy.InjectViewState
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.entity.ApiError
import ru.movista.di.Injector
import ru.movista.domain.usecase.VerifyPhoneUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.custom.FlowRouter
import ru.movista.utils.schedulersIoToMain
import javax.inject.Inject

@InjectViewState
class VerifyPhonePresenter : BasePresenter<VerifyPhoneView>() {

    companion object {
        private const val ERROR_WRONG_CODE_MESSAGE = "E_WRONG_PASSWORD"
    }

    override val screenTag: String
        get() = Screens.VerifyPhone.TAG

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var router: FlowRouter

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var verifyPhoneUseCase: VerifyPhoneUseCase

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.verifyPhoneComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        startResendCodeCount()
    }

    fun onBackPressed() {
        router.exit()
    }

    fun onNextButtonClicked(code: String) {
        addDisposable(
            verifyPhoneUseCase.verifyPhone(code)
                .schedulersIoToMain()
                .doOnSubscribe { viewState.showLoading() }
                .doFinally { viewState.hideLoading() }
                .subscribe(
                    {
                        verifyPhoneUseCase.setUserAuthorized()
                        if (!it.has_email) {
                            router.navigateTo(Screens.Registration())
                        } else {
                            verifyPhoneUseCase.setUserRegistered()
                            verifyPhoneUseCase.saveAccessToken(it.access_token)
                            verifyPhoneUseCase.saveRefreshToken(it.refresh_token)
                            router.finishFlow()
                        }
                        analyticsManager.reportLoginSuccess()
                    },
                    {
                        if (it is ApiError && it.message.contains(ERROR_WRONG_CODE_MESSAGE)) {
                            viewState.showError(resources.getString(R.string.error_wrong_code))
                        } else {
                            viewState.showError(getErrorDescription(it))
                        }
                    }
                )
        )
    }

    fun onResendCodeClicked() {
        addDisposable(
            verifyPhoneUseCase.regenerateCode()
                .schedulersIoToMain()
                .doOnSubscribe { viewState.showLoading() }
                .doFinally { viewState.hideLoading() }
                .subscribe(
                    { startResendCodeCount() },
                    { viewState.showError(getErrorDescription(it)) }
                )
        )
    }

    private fun startResendCodeCount() {
        addDisposable(
            verifyPhoneUseCase.getExpiresInCounter()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        viewState.changeResendCodeText(
                            resources.getString(R.string.resend_code_count, it.toInt()),
                            false
                        )
                    },
                    { },
                    { viewState.changeResendCodeText(resources.getString(R.string.resend_code), true) }
                )
        )
    }
}