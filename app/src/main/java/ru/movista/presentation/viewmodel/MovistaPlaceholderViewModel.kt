package ru.movista.presentation.viewmodel

import android.text.Spanned

data class MovistaPlaceholderViewModel(
    val id: String,
    val optionsCount: Int,
    val optionsCountHint: Spanned,
    val redirectUrl: String
)