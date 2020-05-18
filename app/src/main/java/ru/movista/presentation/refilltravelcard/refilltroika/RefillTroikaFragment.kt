package ru.movista.presentation.refilltravelcard.refilltroika

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_refill_troika.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.refilltravelcard.TroikaViewModel
import ru.movista.presentation.utils.*
import ru.movista.utils.EMPTY

class RefillTroikaFragment : BaseFragment(), RefillTroikaView, OnBackPressedListener {

    companion object {
        private const val KEY_PARAMS = "key_params"

        fun newInstance(troikaViewModel: TroikaViewModel): RefillTroikaFragment {
            return RefillTroikaFragment().apply {
                arguments = Bundle().apply { putSerializable(KEY_PARAMS, troikaViewModel) }
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: RefillTroikaPresenter

    @ProvidePresenter
    fun providePresenter(): RefillTroikaPresenter {
        val troikaViewModel = arguments?.getSerializable(KEY_PARAMS)
        return RefillTroikaPresenter(troikaViewModel)
    }

    override fun getLayoutRes() = ru.movista.R.layout.fragment_refill_troika

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(refill_troika_toolbar)

        refill_troika_parent.setOnClickListener { presenter.onBaseContentClick() }

        refill_troika_card_num_et.onTextChangedListener {
            refill_troika_card_num_til?.error = String.EMPTY
        }

        refill_troika_amount_et.onTextChangedListener {
            if (refill_troika_amount_til?.error != null) {
                refill_troika_amount_til?.error = null
            }
        }

        refill_troika_pay_btn.setOnClickListener {
            presenter.onRefillButtonClicked(
                refill_troika_card_num_et?.text.toString(),
                refill_troika_amount_et?.text.toString()
            )
        }

        prepareWebView()
    }

    override fun onDestroyView() {
        refill_troika_parent.removeAllViews()
        refill_troika_webview.destroy()
        super.onDestroyView()
    }

    override fun onBackPressed() {
        presenter.onBackClicked(refill_troika_webview.isVisible(), refill_troika_webview.canGoBack())
    }

    override fun showLoading() {
        refill_troika_content.setInVisible()
        refill_troika_loading.setVisible()
    }

    override fun hideLoading() {
        refill_troika_content.setVisible()
        refill_troika_loading.setGone()
    }

    override fun setMaxCardLength(maxLength: Int) {
        refill_troika_card_num_et.setMaxLength(maxLength)
    }

    override fun setAmountHelperText(text: String) {
        refill_troika_amount_til.helperText = text
    }

    override fun setMaxAmountLength(maxLength: Int) {
        refill_troika_amount_et.setMaxLength(maxLength)
    }

    override fun showCardNumError(error: String) {
        refill_troika_card_num_til.error = error
    }

    override fun showAmountError(error: String) {
        refill_troika_amount_til.error = error
    }

    override fun loadWebPage(url: String) {
        refill_troika_webview.loadUrl(url)
    }

    override fun hideKeyboard() {
        refill_troika_content.hideSoftKeyboard()
    }

    override fun clearViewsFocus() {
        refill_troika_amount_et.clearFocus()
        refill_troika_card_num_et.clearFocus()
    }

    override fun showError(error: String) {
        refill_troika_content.longSnackbar(error)
    }

    override fun showWebView() {
        refill_troika_content.setGone()
        refill_troika_webview.setVisible()
    }

    override fun closeWebView() {
        refill_troika_webview.clearHistory()
        refill_troika_webview.setGone()
        refill_troika_content.setVisible()
    }

    override fun webViewGoBack() {
        refill_troika_webview.goBack()
    }

    override fun setCardNum(cardNum: String?) {
        refill_troika_card_num_et.setText(cardNum)
    }

    private fun prepareWebView() {
        refill_troika_webview.settings.javaScriptEnabled = true
        refill_troika_webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                presenter.onWebViewPageLoadFinished(url)
            }
        }
    }


}
