package ru.movista.presentation.auth.verifyphone

import ru.movista.presentation.base.BaseLoadingView

interface VerifyPhoneView : BaseLoadingView {
    fun changeResendCodeText(text: String, isClickable: Boolean)
}