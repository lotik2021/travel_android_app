package ru.movista.presentation.viewmodel

import java.io.Serializable

data class AgencyViewModel(
    val title: String,
    val phoneNumber: String = "",
    val url: String = ""
) : Serializable