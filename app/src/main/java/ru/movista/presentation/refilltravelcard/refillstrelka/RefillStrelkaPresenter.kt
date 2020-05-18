package ru.movista.presentation.refilltravelcard.refillstrelka

import android.content.res.Resources
import moxy.InjectViewState
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.entity.ApiError
import ru.movista.data.entity.StrelkaBalanceResponse
import ru.movista.data.repository.RefillTravelCardRepository
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.refilltravelcard.StrelkaViewModel
import ru.movista.presentation.utils.isSuccessPaymentUrl
import ru.movista.utils.reportNullFieldError
import ru.movista.utils.schedulersIoToMain
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

@InjectViewState
class RefillStrelkaPresenter(params: Serializable?) : BasePresenter<RefillStrelkaView>() {

    companion object {
        private var ERROR_WRONG_CARD_NUMBER = "6000"
    }

    override val screenTag: String
        get() = Screens.RefillStrelka.TAG

    private lateinit var strelkaViewModel: StrelkaViewModel

    private var webViewMainPageWasLoaded: Boolean = false
    private var webViewPaymentProceed: Boolean = false

    private var cardTypeId: String? = null

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var refillTravelCardRepository: RefillTravelCardRepository

    @Inject
    lateinit var resources: Resources

    init {
        if (params is StrelkaViewModel) {
            strelkaViewModel = params
        } else {
            Timber.e("Expected TroikaViewModel but was $params")
        }
    }

    override fun onPresenterInject() {
        super.onPresenterInject()

        Injector.init(screenTag)
        Injector.refillStrelkaComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setMaxCardLength(strelkaViewModel.maxLength)
        viewState.setInfoText(strelkaViewModel.hint)
        viewState.setCardNum(refillTravelCardRepository.getStrelkaCardNum())
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

    fun onRequestBalanceClicked(cardNumber: String) {
        if (cardNumber.isEmpty()) {
            viewState.showCardNumError("Пожалуйста, введите номер карты")
            return
        }
        if (cardNumber.length < strelkaViewModel.minLength) {
            viewState.showCardNumError("Минимум ${strelkaViewModel.minLength} цифр")
            return
        } // максимальную длину не проверяем, т.к указали ранее в view

        requestStrelkaBalance(cardNumber)
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

    private fun requestStrelkaBalance(cardNum: String) {

        addDisposable(
            refillTravelCardRepository
                .getStrelkaFullParams(cardNum)
                .schedulersIoToMain()
                .doOnSubscribe {
                    viewState.hideKeyboard()
                    viewState.showLoading()
                }
                .doFinally { viewState.hideLoading() }
                .subscribe(
                    {
                        onStrelkaBalanceReceived(it)
                    },
                    {
                        when (it) {
                            is ApiError -> when (it.code) {
                                ERROR_WRONG_CARD_NUMBER -> {
                                    viewState.showCardNumError(resources.getString(R.string.error_wrong_card_num))
                                }
                                else -> {
                                    viewState.showError(resources.getString(R.string.error_unexpected_error))
                                }
                            }
                            else -> viewState.showError(resources.getString(R.string.error_no_connection))
                        }
                    }
                )
        ).also {
            analyticsManager.reportTravelCardRequestBalance(strelkaViewModel.id, cardNum)
        }
    }

    private fun onStrelkaBalanceReceived(strelkaBalanceResponse: StrelkaBalanceResponse) {
        strelkaViewModel.balance = resources.getString(R.string.strelka_balance, strelkaBalanceResponse.balance.toInt())
        strelkaViewModel.minAmount = strelkaBalanceResponse.parameters.minimum_amount
        strelkaViewModel.maxAmount = strelkaBalanceResponse.parameters.maximum_amount
        cardTypeId = strelkaBalanceResponse.card_type_id

        viewState.showBalance(resources.getString(R.string.strelka_balance, strelkaBalanceResponse.balance.toInt()))
        viewState.setAmountHelperText(
            resources.getString(
                R.string.amount_helper,
                strelkaBalanceResponse.parameters.minimum_amount.toInt(),
                strelkaBalanceResponse.parameters.maximum_amount.toInt()
            )
        )

        viewState.showPayButton()
    }


    private fun validateParams(cardNum: String, amount: Int) {
        if (cardNum.length < strelkaViewModel.minLength) {
            viewState.showCardNumError("Минимум ${strelkaViewModel.minLength} цифр")
            return
        } // максимальную длину не проверяем, т.к указали ранее в view

        val minAmount = strelkaViewModel.minAmount ?: return
        val maxAmount = strelkaViewModel.maxAmount ?: return

        if (minAmount > amount || amount > maxAmount) {
            viewState.showAmountError("Введена некорректная сумма")
            return
        }

        refillStrelka(cardNum, amount)
    }

    private fun refillStrelka(cardNum: String, amount: Int) {
        cardTypeId?.let { cardTypeId ->
            addDisposable(
                refillTravelCardRepository.refillStrelka(cardNum, amount, cardTypeId)
                    .schedulersIoToMain()
                    .doOnSubscribe {
                        viewState.hideKeyboard()
                        viewState.showLoading()
                    }
                    .subscribe(
                        {
                            viewState.loadWebPage(it)
                        },
                        {
                            viewState.hideLoading()
                            viewState.showError(resources.getString(R.string.error_unexpected_error))
                        }
                    )
            ).also {
                analyticsManager.reportTravelCardPay(cardNum, strelkaViewModel.title, amount)
            }
        } ?: reportNullFieldError("cardTypeId")
    }
}