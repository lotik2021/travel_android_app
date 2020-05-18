package ru.movista.presentation.refilltravelcard.refillstrelka

import moxy.MvpView

interface RefillStrelkaView : MvpView {
    fun showError(error: String)

    fun setMaxCardLength(maxLength: Int)
    fun setInfoText(text: String)
    fun setCardNum(cardNum: String?)

    fun setAmountHelperText(text: String)
    fun showBalance(balance: String)

    fun showPayButton()

    fun showCardNumError(error: String)
    fun showAmountError(error: String)
    fun setMaxAmountLength(maxLength: Int)
    fun showLoading()
    fun hideLoading()

    fun hideKeyboard()

    fun showWebView()
    fun closeWebView()
    fun webViewGoBack()

    fun loadWebPage(url: String?)
    fun clearViewsFocus()
}