package ru.movista.presentation.auth.enterphone

import android.content.res.Resources
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import moxy.InjectViewState
import ru.movista.BuildConfig
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.usecase.AuthWithPhoneUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.custom.FlowRouter
import ru.movista.utils.schedulersIoToMain
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class EnterPhonePresenter : BasePresenter<EnterPhoneView>() {

    override val screenTag: String
        get() = Screens.EnterPhone.TAG

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var router: FlowRouter

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var authUseCase: AuthWithPhoneUseCase

    private var phone: String? = null

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.enterPhoneComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun onNextButtonClicked() {
        viewState.verifyWithRecaptcha(BuildConfig.RECAPTCHA_SITE_KEY)
    }

    fun onBackPressed() {
        router.finishFlow()
    }

    fun onPhoneNumberChanged(isPhoneFilled: Boolean, phone: String) {
        this.phone = phone
        viewState.setEnterPhoneNextButtonEnabled(isPhoneFilled)
    }

    fun onRecaptchaSuccess(tokenResult: String?) {
        if (tokenResult?.isNotEmpty() == true) {
            authWithPhone(tokenResult)
            Timber.tag("Recaptcha").i("onRecaptchaSuccess")
        } else {
            viewState.showError(resources.getString(R.string.error_unexpected_error))
            Timber.tag("Recaptcha").e("OnRecaptchaFailure")
        }
    }

    fun onRecaptchaFailure(ex: Exception) {
        var statusMessage = ""
        if (ex is ApiException) {
            statusMessage = CommonStatusCodes.getStatusCodeString(ex.statusCode)
        }

        Timber.tag("Recaptcha").e(ex, "OnRecaptchaFailure: $statusMessage")
        viewState.showError(resources.getString(R.string.error_unexpected_error))
    }

    private fun authWithPhone(recaptchaToken: String) {
        phone?.let { phone ->
            addDisposable(
                authUseCase.authWithPhone(phone, recaptchaToken)
                    .schedulersIoToMain()
                    .doOnSubscribe { viewState.showLoading() }
                    .doFinally { viewState.hideLoading() }
                    .subscribe(
                        {
                            router.navigateTo(Screens.VerifyPhone()).also {
                                analyticsManager.reportLoginEnterPhone()
                            }
                        },
                        { viewState.showError(getErrorDescription(it)) }
                    )
            )
        }
    }
}