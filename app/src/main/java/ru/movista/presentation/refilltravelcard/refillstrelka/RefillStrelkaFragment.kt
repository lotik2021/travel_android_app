package ru.movista.presentation.refilltravelcard.refillstrelka

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_refill_strelka.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.refilltravelcard.StrelkaViewModel
import ru.movista.presentation.utils.*
import ru.movista.utils.EMPTY

class RefillStrelkaFragment : BaseFragment(), RefillStrelkaView, OnBackPressedListener {

    companion object {
        private const val KEY_PARAMS = "key_params"

        fun newInstance(strelkaViewModel: StrelkaViewModel): RefillStrelkaFragment {
            return RefillStrelkaFragment().apply {
                arguments = Bundle().apply { putSerializable(KEY_PARAMS, strelkaViewModel) }
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: RefillStrelkaPresenter

    @ProvidePresenter
    fun providePresenter(): RefillStrelkaPresenter {
        val strelkaViewModel = arguments?.getSerializable(KEY_PARAMS)
        return RefillStrelkaPresenter(strelkaViewModel)
    }

    override fun getLayoutRes() = ru.movista.R.layout.fragment_refill_strelka

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(refill_strelka_toolbar)

        refill_strelka_parent.setOnClickListener { presenter.onBaseContentClick() }

        refill_strelka_card_num_et.onTextChangedListener {
            refill_strelka_card_num_til?.error = String.EMPTY
            refill_strelka_amount_til?.setGone()
            refill_strelka_pay_btn?.setInVisible() // чтобы сохранилась привязка view
            refill_strelka_request_balance?.setVisible()
        }

        refill_strelka_amount_et.onTextChangedListener {
            if (refill_strelka_amount_til?.error != null) {
                refill_strelka_amount_til?.error = null
            }
        }

        refill_strelka_request_balance.setOnClickListener {
            presenter.onRequestBalanceClicked(refill_strelka_card_num_et.text.toString())
        }

        refill_strelka_pay_btn.setOnClickListener {
            presenter.onRefillButtonClicked(
                refill_strelka_card_num_et.text.toString(),
                refill_strelka_amount_et.text.toString()
            )
        }

        prepareWebView()
    }

    override fun onDestroyView() {
        refill_strelka_parent.removeAllViews()
        refill_strelka_webview.destroy()
        super.onDestroyView()
    }

    override fun onBackPressed() {
        presenter.onBackClicked(refill_strelka_webview.isVisible(), refill_strelka_webview.canGoBack())
    }

    override fun setMaxCardLength(maxLength: Int) {
        refill_strelka_card_num_et.setMaxLength(maxLength)
    }

    override fun setAmountHelperText(text: String) {
        refill_strelka_amount_til.helperText = text
    }

    override fun setMaxAmountLength(maxLength: Int) {
        refill_strelka_amount_et.setMaxLength(maxLength)
    }

    override fun showCardNumError(error: String) {
        refill_strelka_card_num_til.error = error
    }

    override fun showAmountError(error: String) {
        refill_strelka_amount_til.error = error
    }

    override fun setInfoText(text: String) {
        refill_strelka_info.text = text
    }

    override fun showBalance(balance: String) {
        refill_strelka_card_num_til.helperText = balance
    }

    override fun showPayButton() {
        refill_strelka_request_balance.setGone()
        refill_strelka_amount_til.setVisible()
        refill_strelka_pay_btn.setVisible()
    }

    override fun showLoading() {
        refill_strelka_content.setGone()
        refill_strelka_loading.setVisible()
    }

    override fun hideLoading() {
        refill_strelka_loading.setGone()
        refill_strelka_content.setVisible()
    }

    override fun clearViewsFocus() {
        refill_strelka_card_num_til.clearFocus()
        refill_strelka_amount_til.clearFocus()
    }

    override fun hideKeyboard() {
        view?.hideSoftKeyboard()
    }

    override fun showError(error: String) {
        refill_strelka_content.longSnackbar(error)
    }

    override fun showWebView() {
        refill_strelka_content.setGone()
        refill_strelka_webview.setVisible()
    }

    override fun loadWebPage(url: String?) {
        refill_strelka_webview.loadUrl(url)
    }

    override fun closeWebView() {
        refill_strelka_webview.clearHistory()
        refill_strelka_webview.setGone()
        refill_strelka_content.setVisible()
    }


    override fun webViewGoBack() {
        refill_strelka_webview.goBack()
    }

    override fun setCardNum(cardNum: String?) {
        refill_strelka_card_num_et.setText(cardNum)
    }

    private fun prepareWebView() {
        refill_strelka_webview.settings.javaScriptEnabled = true
        refill_strelka_webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                presenter.onWebViewPageLoadFinished(url)
            }
        }
    }
}
