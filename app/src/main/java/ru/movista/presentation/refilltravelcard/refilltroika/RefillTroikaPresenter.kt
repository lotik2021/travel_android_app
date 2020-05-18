package ru.movista.presentation.refilltravelcard.refilltroika

import android.content.res.Resources
import moxy.InjectViewState
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.repository.RefillTravelCardRepository
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.refilltravelcard.TroikaViewModel
import ru.movista.presentation.utils.isSuccessPaymentUrl
import ru.movista.utils.schedulersIoToMain
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

@InjectViewState
class RefillTroikaPresenter(params: Serializable?) : BasePresenter<RefillTroikaView>() {

    override val screenTag: String
        get() = Screens.RefillTroika.TAG

    private lateinit var troikaViewModel: TroikaViewModel

    private var webViewMainPageWasLoaded: Boolean = false
    private var webViewPaymentProceed: Boolean = false

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var refillTravelCardRepository: RefillTravelCardRepository

    init {
        if (params is TroikaViewModel) {
            troikaViewModel = params
        } else {
            Timber.e("Expected TroikaViewModel but was $params")
        }
    }

    override fun onPresenterInject() {
        super.onPresenterInject()

        Injector.init(screenTag)
        Injector.refillTroikaComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setMaxCardLength(troikaViewModel.maxLength)
        viewState.setMaxAmountLength(troikaViewModel.maxAmount.toString().length)
        viewState.setCardNum(refillTravelCardRepository.getTroikaCardNum())
        viewState.setAmountHelperText(
            resources.getString(
                R.string.amount_helper,
                troikaViewModel.minAmount,
                troikaViewModel.maxAmount
            )
        )
    }

    fun onBackClicked(webViewIsVisible: Boolean, webViewCanGoBack: Boolean) {
        if (webViewPaymentProceed) {
            router.exit()
            return
        }

        when {
            webViewIsVisible && webViewCanGoBack -> viewState.webViewGoBack()
            webViewIsVisible -> {
                viewState.closeWebView()
                webViewMainPageWasLoaded = false
            }
            else -> router.exit()
        }

    }

    fun onRefillButtonClicked(cardNum: String, amount: String) {
        if (cardNum.isEmpty()) {
            viewState.showCardNumError("Пожалуйста, введите номер карты")
            return
        }

        if (amount.isEmpty()) {
            viewState.showAmountError("Пожалуйста, введите сумму")
            return
        }

        validateParams(cardNum, amount.toInt()) // можно безопастно закастить, т.к в xml указан inputType = "number"

    }

    fun onWebViewPageLoadFinished(url: String?) {
        Timber.i("onWebViewPageLoadFinished: $url")

        if (!webViewMainPageWasLoaded) {
            // не сработает если экран уже закрылся => безопастно вызываем
            viewState.showWebView()
            viewState.hideLoading()
            webViewMainPageWasLoaded = true
        }
        url ?: return
        webViewPaymentProceed = url.isSuccessPaymentUrl()
    }

    fun onBaseContentClick() {
        viewState.hideKeyboard()
        viewState.clearViewsFocus()
    }

    private fun validateParams(cardNum: String, amount: Int) {
        if (cardNum.length < troikaViewModel.minLength) {
            viewState.showCardNumError("Минимум ${troikaViewModel.minLength} цифр")
            return
        } // максимальную длину не проверяем, т.к указали ранее в view

        if (troikaViewModel.minAmount > amount || amount > troikaViewModel.maxAmount) {
            viewState.showAmountError("Введена некорректная сумма")
            return
        }

        refillTroika(cardNum, amount)

    }

    private fun refillTroika(cardNum: String, amount: Int) {
        viewState.hideKeyboard()
        addDisposable(
            refillTravelCardRepository
                .refillTroika(cardNum, amount)
                .schedulersIoToMain()
                .doOnSubscribe { viewState.showLoading() }
                .subscribe(
                    {
                        viewState.loadWebPage(it)
                        viewState.hideKeyboard()
                    },
                    {
                        viewState.hideLoading()
                        viewState.showError(resources.getString(R.string.error_unexpected_error))
                    }
                )
        ).also {
            analyticsManager.reportTravelCardPay(cardNum, troikaViewModel.title, amount)
        }
    }
}