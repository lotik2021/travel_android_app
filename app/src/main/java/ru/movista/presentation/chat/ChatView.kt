package ru.movista.presentation.chat

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import org.threeten.bp.LocalDateTime
import ru.movista.presentation.refilltravelcard.TravelCardViewModel
import ru.movista.presentation.viewmodel.CarRouteViewModel
import ru.movista.presentation.viewmodel.PredefinedUserAction
import ru.movista.presentation.viewmodel.TaxiProviderViewModel

@StateStrategyType(SkipStrategy::class)
interface ChatView : MvpView {

    fun showNewMessages(chatItems: List<ChatItem>)

    fun showAllMessages(chatItems: ArrayList<ChatItem>)

    fun deleteLastMessage(previous: List<ChatItem>)

    fun showUserMessagesChoice(messages: ArrayList<PredefinedUserAction>)

    fun hideUserMessagesChoices()

    fun showTaxiOrderDialog(taxiProviderViewModel: TaxiProviderViewModel)

    fun showCarRouteInfoDialog(carRouteViewModel: CarRouteViewModel)

    fun showRefillTravelCardDialog(travelCardViewModel: ArrayList<TravelCardViewModel>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun disableAnyClicks()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun enableAnyClicks()

    fun requestLocationPermission()

    fun requestAudioPermission()

    fun openWebLink(url: String)

    fun changeInputHint(hint: String)

    fun clearUserMessageField()

    fun hideKeyboard()

    fun setUserMessage(message: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showStartDialogError()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideStartDialogError()

    fun showNoLocationError()

    @StateStrategyType(SkipStrategy::class)
    fun showDatePicker(isShowTime: Boolean, dataSetListener: (LocalDateTime) -> Unit, cancelListener: (() -> Unit))

    fun enableChatSendingButton()
    fun disableChatSendingButton()
    fun startRecordAnimating()
    fun stopRecordAnimating()
    fun clearChat()
    fun removeHistoryAction()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setLongRouteIconVisibility(isAvailableLongRoute: Boolean)
}
