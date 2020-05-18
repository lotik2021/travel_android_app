package ru.movista.data.entity

data class StrelkaBalanceRequest(
    val card_number: String
)

data class StrelkaBalanceResponse(
    val balance: Float,
    val card_type_id: String,
    val parameters: StrelkaBalanceResponseParams,
    val error: ApiError?
)

data class StrelkaBalanceResponseParams(
    val minimum_amount: Float,
    val maximum_amount: Float,
    val minimum_card_length: Int,
    val maximum_card_length: Int
)