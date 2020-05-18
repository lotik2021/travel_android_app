package ru.movista.presentation.refilltravelcard.refilltroika

import moxy.MvpView

interface RefillTroikaView : MvpView {

    fun setMaxCardLength(maxLength: Int)

    fun setAmountHelperText(text: String)

    fun showCardNumError(error: String)
    fun showAmountError(error: String)
    fun setMaxAmountLength(maxLength: Int)
    fun setCardNum(cardNum: String?)

    fun showLoading()
    fun hideLoading()
    fun loadWebPage(url: String)
    fun hideKeyboard()

    fun showError(error: String)

    fun showWebView()
    fun closeWebView()
    fun webViewGoBack()
    fun clearViewsFocus()
}