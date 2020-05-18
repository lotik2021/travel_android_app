package ru.movista.presentation.refilltravelcard

import java.io.Serializable

sealed class TravelCardViewModel(
    val cardId: Int,
    val cardTitle: String,
    val cardPurpose: String,
    val cardHint: String
) : Serializable

data class TroikaViewModel(
    val id: Int,
    val title: String,
    val purpose: String,
    val hint: String,
    val maxLength: Int,
    val minLength: Int,
    val maxAmount: Int,
    val minAmount: Int
) : TravelCardViewModel(id, title, purpose, hint), Serializable

data class StrelkaViewModel(
    val id: Int,
    val title: String,
    val purpose: String,
    val hint: String,
    val maxLength: Int,
    val minLength: Int,
    var balance: String? = null,
    var minAmount: Float? = null,
    var maxAmount: Float? = null
) : TravelCardViewModel(id, title, purpose, hint), Serializable