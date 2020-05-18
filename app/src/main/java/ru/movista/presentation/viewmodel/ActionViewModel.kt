package ru.movista.presentation.viewmodel

import ru.movista.data.entity.ActionEntity

sealed class ActionViewModel

data class EnteredUserMessageAction(val message: String) : ActionViewModel()

data class PredefinedUserAction(val actionEntity: ActionEntity) : ActionViewModel()

data class ChooseAddressFromChatAction(
    val addressViewModel: ChatAddressViewModel,
    val actionId: String,
    val googleToken: String
) : ActionViewModel()