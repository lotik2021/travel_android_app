package ru.movista.presentation.chat

import android.content.res.Resources
import android.speech.SpeechRecognizer
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import org.threeten.bp.LocalDateTime
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.entity.*
import ru.movista.data.framework.*
import ru.movista.data.source.local.*
import ru.movista.di.Injector
import ru.movista.domain.model.Place
import ru.movista.domain.model.TripTime
import ru.movista.domain.usecase.ActionsUseCase
import ru.movista.domain.usecase.IChoosePlaceUseCase
import ru.movista.domain.usecase.SearchPlaceUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.common.OnAudioPermissionResultListener
import ru.movista.presentation.common.OnLocationPermissionResultListener
import ru.movista.presentation.converter.ChatMessagesConverter
import ru.movista.presentation.converter.ExtrasConverter
import ru.movista.presentation.converter.RouteViewModelConverter
import ru.movista.presentation.placesearch.UserHistoryTab
import ru.movista.presentation.refilltravelcard.TravelCardViewModel
import ru.movista.presentation.utils.TravelCardsManager
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.presentation.viewmodel.*
import ru.movista.utils.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChatPresenter :
    BasePresenter<ChatView>(),
    OnLocationPermissionResultListener,
    OnAudioPermissionResultListener {

    companion object {
        private const val DATE_TIME_FOR_USER_PATTERN = "dd MMMM в HH:mm"
        private const val DATE_FOR_USER_PATTERN = "dd MMMM yyyy"
    }

    override val screenTag: String
        get() = Screens.Chat.TAG

    private var wasFirstViewAttach = false

    private val chatMessages: ArrayList<ChatItem> = arrayListOf()

    /**
     * Флаг, определяющий текущую доступность action'ов для пользователя
     */
    private var actionsAreDisabled = true

    private val currentAvailableUserActions: ArrayList<PredefinedUserAction> = arrayListOf()

    private var placeFoundDisposable: Disposable? = null
    private var placeFrom: Place? = null
    private var placeTo: Place? = null

    private var departureTime: LocalDateTime? = null
    private var arrivalTime: LocalDateTime? = null

    private var sessionIsCreated: Boolean? = null


    /**
     * Текущий выбранный пользователем action. Присваеваем ему значение null, когда выбранный action отправляемся на сервер
     */
    private var currentChosenAction: ActionViewModel? = null

    private var currentUserMessageHint: String = resources.getString(R.string.user_message_default_hint)

    /**
     * Флаг, указывающий на то, что handable action уже обработан локально
     */
    private var lastHandableActionIsHandled: Boolean = false

    /**
     * Флаг, указывающий на то, что handable action в обработке уже не нуждается
     * Например, юзер прервал сценарий обработки action'a или же в ходе его обработки возникла ошибка
     */
    private var lastHandableActionNotNeedToBeHandled: Boolean = false

    private var messagesToShowOneByOne = arrayListOf<ChatItem>()

    private var speechRecognitionDisposable: Disposable? = null

    /**
     * Флаг отображения пикера даты / времени. Нужен чтобы не показывать пикер повторно при возврате на экран
     */
    private var dateTimePickerIsShowing = false

    private var isAvailableLongRoute = true

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var actionsUseCase: ActionsUseCase

    @Inject
    lateinit var searchPlaceUseCase: SearchPlaceUseCase

    @Inject
    lateinit var travelCardsManager: TravelCardsManager

    @Inject
    lateinit var chatMessagesConverter: ChatMessagesConverter

    @Inject
    lateinit var routeViewModelConverter: RouteViewModelConverter

    @Inject
    lateinit var extrasConverter: ExtrasConverter

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var speechRecognitionManager: SpeechRecognitionManager

    override fun onPresenterInject() {
        Injector.chatComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (wasFirstViewAttach) return

        viewState.disableChatSendingButton()
        chatMessages.add(LoadingBotMessage)
        showNewMessages(1)
        checkLongRouteState()
    }

    override fun attachView(view: ChatView) {
        super.attachView(view)

        if (wasFirstViewAttach) {
            Timber.i("attachView: showAllMessages")
            viewState.showAllMessages(chatMessages)
        } else {
            wasFirstViewAttach = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        placeFoundDisposable?.dispose()
        speechRecognitionDisposable?.dispose()
    }

    override fun onLocationPermissionReceived(lat: Double, lon: Double) {
        Timber.d("onLastLocationReceived")
        onUserLocationReceived(lat, lon)
    }

    override fun onLastLocationPermissionDenied() {
        Timber.d("onLastLocationPermissionDenied")
        lastHandableActionNotNeedToBeHandled = true
        removeOnlyLoadingMessage()
    }

    override fun onNeverAskLocationClicked() {
        Timber.i("onAccessPermissionClick")
        lastHandableActionNotNeedToBeHandled = true
    }

    override fun onNeverAskLocationLaterClicked() {
        Timber.i("onLaterAccessPermissionClick")
        lastHandableActionNotNeedToBeHandled = true
        removeOnlyLoadingMessage()
    }

    override fun onAudioPermissionGranted() {
        startSpeechRecognizing()
    }

    fun onAllMessagesShown(chatItems: List<ChatItem>) {
        // сначала проверяем была ли попытка создать сессию
        if (sessionIsCreated == null) {
            createSession()
            return
        }

        // проверяем, если ли сообщения в очереди для показа с анимацией
        if (messagesToShowOneByOne.isNotEmpty()) {
            val size = messagesToShowOneByOne.size
            chatMessages.addAll(messagesToShowOneByOne)
            messagesToShowOneByOne.clear()
            showNewMessages(size)
            return
        } // есть еще сообщения для показа с анимацией - ждем окончания

        val lastMessage = chatItems.lastOrNull()

        Timber.i("Last message was $lastMessage")

        lastMessage ?: return

        when (lastMessage) {

            is UserMessage -> {
                // было показано сообщение пользователя
                if (lastHandableActionNotNeedToBeHandled) {
                    // заново предлагаем actions
                    changeAvailableActionsState(false)
                } else {
                    currentChosenAction?.let {
                        if (it is PredefinedUserAction) {
                            // todo тут нужен жесткий рефакторинг
                            if (
                                it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.CHOOSE_ON_MAP
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.ENTER_ADDRESS
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.UPDATE_APP
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.DEPARTURE_TIME
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.ARRIVAL_TIME
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.SPECIFY_DATE
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.SHOW_MAP
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.FAVORITE_ADDRESS
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.PLAN_MULTIMODAL_ROUTE
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.BUY_TICKET_FLIGHT
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.BUY_TICKET_TRAIN
                                || it.actionEntity.action_id == ActionIdsThatNotRequiredLoading.BUY_TICKET_BUS
                            ) {
                                // обрабатываем эти actions без загрузки
                                handleUserAction(it)
                            } else {
                                // показываем загрузку для дальнейшей обработки action
                                chatMessages.add(LoadingBotMessage)
                                showNewMessages(1)

                                changeAvailableActionsState(true)
                            }
                        } else {
                            // показываем загрузку для дальнейшей обработки action
                            chatMessages.add(LoadingBotMessage)
                            showNewMessages(1)

                            changeAvailableActionsState(true)
                        }
                    } ?: reportNullFieldError("currentChosenAction")
                }
            }
            is LoadingBotMessage -> {
                // была показана загрузка - обрабатываем action
                currentChosenAction?.let {
                    handleUserAction(it)
                }
                    ?: Timber.i("Action to handle is null - waiting for response") // сообщение уже обработано - ждем результата
                changeAvailableActionsState(true)
            }
            is MapItem -> {
                // показываем загрузку для дальнейшей обработки action
                chatMessages.add(LoadingBotMessage)
                showNewMessages(1)
            }
            else -> {
                // было показано сообщение от бота
                changeAvailableActionsState(false)
            }
        }
    }

    fun onChatRefreshClicked() {
        Timber.i("onChatRefreshClicked")
        viewState.hideStartDialogError()
        sessionIsCreated = null

        chatMessages.add(LoadingBotMessage)
        showNewMessages(1)
    }

    fun onUserMessageChoiceClicked(index: Int) {

        if (actionsAreDisabled) {
            Timber.i("Actions currently disabled - do not handle")
            return
        }

        val chosenAction = currentAvailableUserActions.getOrNull(index)

        if (chosenAction == null) {
            reportNullFieldError("chosenAction")
            return
        }

        when {
            chosenAction.actionEntity.action_id == HandableActionIds.CLEAR_CHAT -> {
                viewState.clearChat()
                chatMessages.clear()
                viewState.removeHistoryAction()
                currentAvailableUserActions.removeAll { it.actionEntity.action_id == HandableActionIds.CLEAR_CHAT }
            }
            HandableActionIds.isLongRouteAction(chosenAction.actionEntity.action_id) && !isAvailableLongRoute -> {
                addChosenAction(getBuildRoute(chosenAction.actionEntity.title))
            }
            else -> addChosenAction(chosenAction)
        }
    }

    fun onUserMessageSendClicked(message: String) {
        currentChosenAction = EnteredUserMessageAction(message)

        viewState.clearUserMessageField()

        chatMessages.add(UserMessage(message))

        afterUserMessageAdded()
    }

    fun onTaxiItemClicked(chatIndex: Int, taxiRouteIndex: Int) {
        Timber.i("onTaxiItemClicked")

        val taxiItem = getChatItemIfExists<TaxiItem>(chatIndex) ?: return
        val chosenTaxiRoute =
            getExpectedItemFromList<TaxiProviderViewModel>(taxiRouteIndex, taxiItem.taxiProviders) ?: return

        router.navigateTo(Screens.RouteDetail(chosenTaxiRoute))
            .also { analyticsManager.reportSelectTaxiRoute(chosenTaxiRoute.taxiProviderId, taxiRouteIndex) }
    }

    fun onRouteItemClicked(chatIndex: Int, routeItemIndex: Int) {
        Timber.i("onRouteItemClicked")

        val routesItem = getChatItemIfExists<RoutesItem>(chatIndex) ?: return

        val chosenRoute = getExpectedItemFromList<RouteViewModel>(
            routeItemIndex,
            routesItem.routes
        ) ?: return

        router.navigateTo(
            Screens
                .RouteDetail(routeViewModelConverter.routeToRouteDetailsViewModel(chosenRoute))
                .also { analyticsManager.reportSelectRouteTransport(chosenRoute.id, routeItemIndex) }
        )
    }

    fun onCarRouteClicked(chatIndex: Int, carRouteIndex: Int) {
        Timber.i("onCarRouteClicked")

        val carItem = getChatItemIfExists<CarItem>(chatIndex) ?: return

        val chosenCarRoute = getExpectedItemFromList<CarRouteViewModel>(
            carRouteIndex,
            carItem.carRoutes
        ) ?: return

        router.navigateTo(Screens.RouteDetail(chosenCarRoute))
            .also { analyticsManager.reportSelectCarRoute(chosenCarRoute.carId, carRouteIndex) }
    }

    fun onLongRouteItemClicked(chatIndex: Int, longRouteIndex: Int) {
        Timber.i("onLongRouteItemClicked")

        val longRoutesItem = getChatItemIfExists<LongRoutesItem>(chatIndex) ?: return

        val chosenLongRouteItem = getExpectedItemFromList<MovistaRouteViewModel>(
            longRouteIndex,
            longRoutesItem.longRoutes
        ) ?: return

        viewState.openWebLink("${chosenLongRouteItem.redirectUrl}&$DEFAULT_UTM_SOURCE")
            .also { analyticsManager.reportSelectRouteMovista(chosenLongRouteItem.id, longRouteIndex) }
    }

    fun onMovistaPlaceholderClicked(chatIndex: Int) {
        Timber.i("onMovistaPlaceholderClicked")

        val chosenItem = getChatItemIfExists<MovistaPlaceholderItem>(chatIndex) ?: return

        viewState.openWebLink(chosenItem.placeholder.redirectUrl)
            .also {
                analyticsManager.reportSelectMovistaPlaceholder(
                    chosenItem.placeholder.id,
                    chosenItem.placeholder.optionsCount
                )
            }
    }

    fun onSkillMessageClicked(chatIndex: Int, skillIndex: Int) {
        Timber.i("onSkillMessageClicked : position $chatIndex")

        if (actionsAreDisabled) {
            Timber.i("Actions currently disabled - do not handle")
            return
        }

        val chosenItem = getChatItemIfExists<SkillsItem>(chatIndex) ?: return
        val chosenSkill = getExpectedItemFromList<SkillViewModel>(skillIndex, chosenItem.skills)
        chosenSkill ?: return

        val chosenActionId = chosenSkill.actionId

        var chosenAction = currentAvailableUserActions.find {
            it.actionEntity.action_id == chosenActionId
        }

        if (chosenAction == null) {
            // нет выбранного скилла в доступных actions
            Timber.i("chosen skill action is absent")
            // создаем сами action из выбранного скила
            chosenAction = createActionFromSkillItem(chosenItem)
            // очищаем значения, которые мог выбрать пользователь, т.к после выбора skill сценарий начинается с начала
            clearChosenPlaces()
            clearChosenTime()
        }

        if (HandableActionIds.isLongRouteAction(chosenAction.actionEntity.action_id) && !isAvailableLongRoute) {
            addChosenAction(getBuildRoute(chosenAction.actionEntity.title))
        } else {
            addChosenAction(chosenAction)
        }
    }

    fun onAddressClicked(chatIndex: Int, addressIndex: Int) {
        Timber.i("onAddressClicked : address position $addressIndex")

        if (actionsAreDisabled) {
            Timber.i("Actions currently disabled - do not handle")
            return
        }

        // проверяем, есть ли сейчас в доступных actions ввод адреса (если нет - значит чат ушел дальше, сценарий закончен)
        currentAvailableUserActions.find { it.actionEntity.action_id == HandableActionIds.ENTER_ADDRESS } ?: return

        val addressItem = getChatItemIfExists<AddressesItem>(chatIndex) ?: return

        val chosenAddress = getExpectedItemFromList<ChatAddressViewModel>(addressIndex, addressItem.addresses)
        chosenAddress ?: return

        actionsAreDisabled = true

        val addressFromChatAction =
            ChooseAddressFromChatAction(chosenAddress, HandableActionIds.ENTER_ADDRESS, addressItem.googleToken)

        currentChosenAction = addressFromChatAction

        chatMessages.add(LoadingBotMessage)

        showNewMessages(1)
    }

    fun onAppInfoClick() {
        Timber.i("onAppInfoClick")
        router.navigateTo(Screens.Profile()).also {
            analyticsManager.reportOpenProfile()
        }
    }

    fun onRecognizeSpeechClicked() {
        if (!speechRecognitionManager.isRecordAudioPermissionGranted()) {
            viewState.requestAudioPermission()
        } else {
            startSpeechRecognizing()
        }
    }

    fun onLongRouteScreenClick() {
        navigateToSearchTickets()
    }

    private fun navigateToSearchTickets() {
        router.navigateTo(Screens.SearchTickets())
    }

    private fun createSession() {
        Timber.i("creating session")

        sessionIsCreated = false

        addDisposable(
            actionsUseCase.createSession()
                .schedulersIoToMain()
                .subscribe(
                    {
                        sessionIsCreated = true
                        onDialogResponseSuccess(it)
                    },
                    {
                        onCreateSessionFailure(it)
                    }
                )
        )
    }

    private fun onDialogResponseSuccess(response: DialogResponse) {
        val actionIds = arrayListOf<String>()

        response.actions?.forEach {
            actionIds.add(it.action_id)
        }

        if (!actionIds.any { it == HandableActionIds.CHOOSE_ON_MAP || it == HandableActionIds.ENTER_ADDRESS || it == HandableActionIds.FAVORITE_ADDRESS}) {
            // составной сценарий поиска маршрута по точка прерван - обнуляем значения
            clearChosenPlaces()
        }

        val tripTimeCondition = actionIds.any {
            it == HandableActionIds.DEPARTURE_TIME
                    || it == HandableActionIds.ARRIVAL_TIME
                    || it == HandableActionIds.SPECIFY_DATE
        }

        if (!tripTimeCondition) {
            // составной сценарий поиска маршрута по точка прерван - обнуляем значения
            clearChosenTime()
        }

        val botItems = chatMessagesConverter.objectsToChatItems(response.objects)
        chatMessages.addAll(botItems)

        response.actions?.let { list ->
            currentAvailableUserActions.clear()
            list.forEach {
                currentAvailableUserActions.add(PredefinedUserAction(it))
            }
        } ?: reportNullFieldError("response.actions")

        when {
            searchRoutesIsAvailable(currentAvailableUserActions) -> {
                // если есть вариант найти маршруты
                // симулируем выбор этого action, чтобы запустить сценарий поиска маршрутов
                currentChosenAction = currentAvailableUserActions[0]

                chatMessages.removeAll { it is LoadingBotMessage }
                chatMessages.add(LoadingBotMessage)
                showNewMessages(botItems.size + 1) // + загрузка
            }
            showTravelCardsCommandReceived(response.objects) -> {
                // пришла команда показать список транспортных карт
                val travelCardsResponse = (response.objects?.get(0) as AvailableTravelCardsEntity).data
                showAvailableTravelCards(
                    travelCardsManager.getAvailableTravelCardsFromTravelCardsResponse(
                        travelCardsResponse
                    )
                )
            }
            else -> {
                chatMessages.removeAll { it is LoadingBotMessage }
                showNewMessages(botItems.size)
            }
        }


        response.extras?.let {
            val extras = extrasConverter.toExtrasViewModel(it)
            dealWithExtras(extras)
        }

        response.hint?.let {
            dealWithHint(it)
        } ?: also { currentUserMessageHint = resources.getString(R.string.user_message_default_hint) }

        lastHandableActionIsHandled = false
    }

    private fun onCreateSessionFailure(error: Throwable) {
        Timber.w("onCreateSessionFailure: $error")
        if (error is ApiError) Timber.e("onCreateSessionError $error")

        removeOnlyLoadingMessage()
        viewState.showStartDialogError()
        viewState.enableAnyClicks()
    }

    private fun onDialogResponseFailure(error: Throwable) {
        Timber.w("onDialogResponseFailure: $error")
        chatMessages.removeAll { it is LoadingBotMessage }

        if (error is ApiError) {
            Timber.e("Server error: $error")
            chatMessages.add(BotMessage(resources.getString(R.string.error_server_unavailable)))
        } else {
            chatMessages.add(BotMessage(resources.getString(R.string.error_no_connection)))
        }

        showNewMessages(1)
    }

    private fun dealWithExtras(extras: ExtrasViewModel) {
        when (extras) {
            is ToLocationExtrasViewModel -> placeTo = Place(lat = extras.lat, lon = extras.lon)
        }
    }

    private fun dealWithHint(hint: String) {
        currentUserMessageHint = if (hint.isNotEmpty()) {
            hint
        } else {
            resources.getString(R.string.user_message_default_hint)
        }
    }

    private fun addChosenAction(chosenAction: PredefinedUserAction) {
        Timber.i("addChosenAction : $chosenAction")

        actionsAreDisabled = true

        this.currentChosenAction = chosenAction

        chatMessages.add(UserMessage(chosenAction.actionEntity.title))

        afterUserMessageAdded()
    }

    private fun afterUserMessageAdded() {

        lastHandableActionNotNeedToBeHandled = false
        lastHandableActionIsHandled = false


        Timber.i("User chose $currentChosenAction")

        viewState.disableAnyClicks()
        showNewMessages(1)
        viewState.hideKeyboard()
    }


    private fun searchRoutesIsAvailable(currentAvailableUserActions: ArrayList<PredefinedUserAction>): Boolean {
        return currentAvailableUserActions.size == 1
                && currentAvailableUserActions[0].actionEntity.action_id == ActionIds.SEARCH_ROUTES
    }

    private fun showTravelCardsCommandReceived(objects: List<ObjectEntity>?): Boolean {
        return objects?.size == 1
                && objects[0].object_id == ObjectType.AVAILABLE_TRAVEL_CARDS
    }

    private fun handleUserAction(chosenAction: ActionViewModel) {
        Timber.i("Handle user action: $chosenAction")

        lastHandableActionNotNeedToBeHandled = false

        when (chosenAction) {
            is PredefinedUserAction -> handlePredefinedUserAction(chosenAction)
            is EnteredUserMessageAction -> sendEnteredUserMessage(chosenAction)
            is ChooseAddressFromChatAction -> handleChooseAddressFromChatAction(chosenAction)
        }
    }

    private fun handlePredefinedUserAction(predefinedUserAction: PredefinedUserAction) {
        when {
            predefinedUserAction.actionEntity.handable -> {
                when {
                    lastHandableActionIsHandled -> {
                        Timber.i("Chosen action is handled")

                        when (predefinedUserAction.actionEntity.action_id) {
                            HandableActionIds.CHOOSE_ON_MAP,
                            HandableActionIds.ENTER_ADDRESS,
                            HandableActionIds.FAVORITE_ADDRESS,
                            HandableActionIds.FROM_CURRENT_LOCATION -> {
                                // пользователь уже выбрал локацию -> отослать выбранную локацию на сервер
                                sendChosenPlace(predefinedUserAction.actionEntity.action_id)
                            }
                            HandableActionIds.DEPARTURE_TIME,
                            HandableActionIds.ARRIVAL_TIME,
                            HandableActionIds.SPECIFY_DATE -> {
                                // пользователь уже выбрал время -> отослать выбранное время на сервер
                                sendChosenTripTime(predefinedUserAction.actionEntity.action_id)
                            }
                        }
                    }
                    else -> {
                        Timber.i("Chosen action is not handled")

                        when (predefinedUserAction.actionEntity.action_id) {
                            HandableActionIds.REFILL_TRAVEL_CARD -> getAvailableTravelCards()
                            HandableActionIds.CHOOSE_ON_MAP -> onChoosePlaceOnMapClicked()
                            HandableActionIds.ENTER_ADDRESS -> onSearchPlaceClicked()
                            HandableActionIds.FAVORITE_ADDRESS -> openFavoriteAddresses()
                            HandableActionIds.SHOW_MAP -> openMap()
                            HandableActionIds.PLAN_MULTIMODAL_ROUTE -> openLongRouteScreen()
                            HandableActionIds.BUY_TICKET_FLIGHT -> openLongRouteScreen()
                            HandableActionIds.BUY_TICKET_TRAIN -> openLongRouteScreen()
                            HandableActionIds.BUY_TICKET_BUS -> openLongRouteScreen()
                            HandableActionIds.FROM_CURRENT_LOCATION -> {
                                if (locationManager.isLocationPermissionGranted()) {
                                    getLastKnownLocation()
                                } else {
                                    viewState.requestLocationPermission()
                                }
                            }
                            HandableActionIds.UPDATE_APP -> {
                                viewState.openWebLink(GOOGLE_PLAY_APP_LINK)
                            }
                            HandableActionIds.DEPARTURE_TIME -> {
                                if (dateTimePickerIsShowing) return
                                dateTimePickerIsShowing = true
                                viewState.showDatePicker(
                                    isShowTime = true,
                                    dataSetListener = { date ->
                                        Timber.i("user chose departureTime")
                                        dateTimePickerIsShowing = false
                                        departureTime = date
                                        onTripDateTimeSelected(date)
                                    },
                                    cancelListener = {
                                        Timber.i("user abort choose departureTime")
                                        dateTimePickerIsShowing = false
                                        lastHandableActionNotNeedToBeHandled = true
                                        changeAvailableActionsState(false)
                                    }
                                )
                            }
                            HandableActionIds.ARRIVAL_TIME -> {
                                if (dateTimePickerIsShowing) return
                                dateTimePickerIsShowing = true
                                viewState.showDatePicker(
                                    isShowTime = true,
                                    dataSetListener = { date ->
                                        Timber.i("user chose arrivalTime")
                                        arrivalTime = date
                                        dateTimePickerIsShowing = false
                                        onTripDateTimeSelected(date)
                                    },
                                    cancelListener = {
                                        Timber.i("user abort choose arrivalTime")
                                        lastHandableActionNotNeedToBeHandled = true
                                        dateTimePickerIsShowing = false
                                        changeAvailableActionsState(false)
                                    }
                                )
                            }
                            HandableActionIds.SPECIFY_DATE -> {
                                if (dateTimePickerIsShowing) return
                                dateTimePickerIsShowing = true
                                viewState.showDatePicker(
                                    isShowTime = false,
                                    dataSetListener = { date ->
                                        Timber.i("user chose arrivalTime")
                                        departureTime = date
                                        dateTimePickerIsShowing = false
                                        onTripDateLongDistanceSelected(date)
                                    },
                                    cancelListener = {
                                        Timber.i("user abort choose arrivalTime")
                                        lastHandableActionNotNeedToBeHandled = true
                                        dateTimePickerIsShowing = false
                                        changeAvailableActionsState(false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                sendChosenAction(predefinedUserAction)
            }
        }
    }

    private fun openLongRouteScreen() {
        lastHandableActionNotNeedToBeHandled = true
        navigateToSearchTickets()
    }

    private fun openMap() {
        Injector.init(Screens.SelectPlaceOnMap.TAG)
        val choosePlaceUseCase = Injector.selectPlaceOnMapComponent?.getChoosePlaceUseCase() ?: return

        placeFoundDisposable =
            choosePlaceUseCase.getOnPlaceFoundObservable()
                .subscribe(
                    {},
                    {
                        lastHandableActionNotNeedToBeHandled = true
                        Timber.i("back to chat")
                    }
                )

        router.navigateTo(Screens.SelectPlaceOnMap(isSimpleMap = true))
    }

    private fun openFavoriteAddresses() {
        Injector.init(Screens.PlaceSearch.TAG) // инициализируем создание компонента, для последующей подписки на результат

        val choosePlaceUseCase = Injector.searchPlaceComponent?.getChoosePlaceUseCase() ?: return

        subscribeToPlaceFoundEvent(choosePlaceUseCase)
        router.navigateTo(Screens.PlaceSearch(userHistoryTab = UserHistoryTab.FAVORITES))

//        viewState.hideUserMessagesChoices()
    }

    private fun onChoosePlaceOnMapClicked() {

        Injector.init(Screens.SelectPlaceOnMap.TAG) // инициализируем создание map-компонента, для последующей подписки на результат

        val choosePlaceUseCase =
            Injector.selectPlaceOnMapComponent?.getChoosePlaceUseCase() ?: return

        subscribeToPlaceFoundEvent(choosePlaceUseCase)
        router.navigateTo(Screens.SelectPlaceOnMap())

//        viewState.hideUserMessagesChoices()
    }

    private fun onTripDateTimeSelected(date: LocalDateTime) {
        Timber.i("onTripDateTimeSelected")
        lastHandableActionIsHandled = true

        val formattedDateTime = fromDateToString(date, DATE_TIME_FOR_USER_PATTERN)
        chatMessages.add(UserMessage(formattedDateTime))

        showNewMessages(1)
    }

    private fun onTripDateLongDistanceSelected(date: LocalDateTime) {
        Timber.i("onTripDateLongDistanceSelected")
        lastHandableActionIsHandled = true

        val formattedDateTime = fromDateToString(date, DATE_FOR_USER_PATTERN)
        chatMessages.add(UserMessage(formattedDateTime))

        showNewMessages(1)
    }

    private fun onUserChoosePlace(place: Place) {
        Timber.i("onUserChoosePlace")
        lastHandableActionIsHandled = true

        if (placeTo == null) {
            Timber.i("user chose to place")
            placeTo = place
        } else {
            Timber.i("user chose from place")
            placeFrom = place
        }

        if (attachedViews.isEmpty()) {
            // запланировать показ сообщений одного за другим
            if (place.name.isNotEmpty()) messagesToShowOneByOne.add(UserMessage(place.name))
            messagesToShowOneByOne.add(
                MapItem(
                    placeFrom?.lat,
                    placeFrom?.lon,
                    placeTo?.lat,
                    placeTo?.lon
                )
            )
        } else {
            if (place.name.isNotEmpty()) {
                chatMessages.add(UserMessage(place.name))
            }
            chatMessages.add(
                MapItem(
                    placeFrom?.lat,
                    placeFrom?.lon,
                    placeTo?.lat,
                    placeTo?.lon
                )
            )
            showNewMessages(if (place.name.isNotEmpty()) 2 else 1)
        }
    }

    private fun onSearchPlaceClicked() {
        Injector.init(Screens.PlaceSearch.TAG) // инициализируем создание компонента, для последующей подписки на результат

        val choosePlaceUseCase = Injector.searchPlaceComponent?.getChoosePlaceUseCase() ?: return

        subscribeToPlaceFoundEvent(choosePlaceUseCase)
        router.navigateTo(Screens.PlaceSearch(userHistoryTab = UserHistoryTab.RECENT))

//        viewState.hideUserMessagesChoices()
    }

    private fun subscribeToPlaceFoundEvent(choosePlaceUseCase: IChoosePlaceUseCase) {
        placeFoundDisposable =
            choosePlaceUseCase.getOnPlaceFoundObservable()
                .subscribe(
                    {
                        onUserChoosePlace(it)
                    },
                    {
                        Timber.i("user abort choose place")
                        lastHandableActionNotNeedToBeHandled = true
                    }
                )
    }

    private fun showNewMessages(itemsAdded: Int) {
        val subList = chatMessages.subList(chatMessages.size - itemsAdded, chatMessages.size)
        val messagesToShow = arrayListOf<ChatItem>().apply { addAll(subList) }
        Timber.i("Messages to show: $messagesToShow")

        if (messagesToShow.contains(LoadingBotMessage)) {
            // блочим клики если пошла загрузка сообщений
            Timber.i("messagesToShow contains LoadingBotMessage - block any clicks")
            viewState.disableAnyClicks()
        }

        postOnMainThread { viewState.showNewMessages(messagesToShow) }
    }

    private fun changeAvailableActionsState(shouldBeDisabled: Boolean) {
        actionsAreDisabled = shouldBeDisabled

        if (!shouldBeDisabled) {
            Timber.i("Actions to show: $currentAvailableUserActions")
            viewState.showUserMessagesChoice(currentAvailableUserActions)
            onAvailableUserActionsShown()
        } else {
            Timber.i("Hide available user actions")
            viewState.hideUserMessagesChoices()
        }

        viewState.changeInputHint(currentUserMessageHint)
    }


    private fun onAvailableUserActionsShown() {
        Timber.i("Available user actions shown - enable clicks")

        viewState.enableAnyClicks()
    }

    private fun removeOnlyLoadingMessage() {
        Timber.i("Removing loading message")

        if (chatMessages.last() !is LoadingBotMessage) {
            Timber.e(
                "Loading message is not persist or is not last in chat messages $chatMessages list"
            )
            return
        }
        chatMessages.remove(LoadingBotMessage)
        viewState.deleteLastMessage(if (chatMessages.isNotEmpty()) listOf(chatMessages.last()) else emptyList())
    }


    private fun sendChosenAction(chosenAction: PredefinedUserAction) {
        Timber.i("sendChosenAction: $chosenAction")

        currentChosenAction = null // сценарий закончен - дожидаемся ответа от сервера
        addDisposable(
            actionsUseCase.sendChosenAction(chosenAction.actionEntity.action_id)
                .schedulersIoToMain()
                .subscribe(
                    { onDialogResponseSuccess(it) },
                    { onDialogResponseFailure(it) }
                )
        ).also { analyticsManager.reportSelectAction(chosenAction.actionEntity.action_id) }
    }

    private fun sendChosenActionWithClientLocation(chosenActionId: String, location: Place) {
        Timber.i("sendChosenActionWithClientLocation: $location")

        currentChosenAction = null // сценарий закончен - дожидаемся ответа от сервера
        addDisposable(
            actionsUseCase.sendChosenAction(chosenActionId, location)
                .schedulersIoToMain()
                .subscribe(
                    { onDialogResponseSuccess(it) },
                    { onDialogResponseFailure(it) }
                )
        )
    }

    private fun sendChosenActionWithClientTripTime(chosenActionId: String, tripTime: LocalDateTime) {
        val formattedTripTime = fromDateToString(tripTime, GMT_DATE_TIME_FORMAT)

        Timber.i("sendChosenActionWithClientTripTime: $formattedTripTime")

        currentChosenAction = null // сценарий закончен - дожидаемся ответа от сервера
        addDisposable(
            actionsUseCase.sendChosenAction(chosenActionId, TripTime(formattedTripTime))
                .schedulersIoToMain()
                .subscribe(
                    { onDialogResponseSuccess(it) },
                    { onDialogResponseFailure(it) }
                )
        )
    }

    private fun sendEnteredUserMessage(enteredUserMessageAction: EnteredUserMessageAction) {
        Timber.i("sendEnteredUserMessage: $enteredUserMessageAction")

        currentChosenAction = null // сценарий закончен - дожидаемся ответа от сервера
        addDisposable(
            actionsUseCase
                .sendEnteredUserMessage(enteredUserMessageAction.message)
                .schedulersIoToMain()
                .subscribe(
                    { onDialogResponseSuccess(it) },
                    { onDialogResponseFailure(it) }
                )
        ).also { analyticsManager.reportUserSendMessage(enteredUserMessageAction.message) }
    }


    private fun getAvailableTravelCards() {
        Timber.i("getting AvailableTravelCards...")
        currentChosenAction = null // сценарий закончен - дожидаемся ответа от сервера
        addDisposable(
            travelCardsManager.getAvailableTravelCards()
                .schedulersIoToMain()
                .subscribe(
                    { showAvailableTravelCards(it) },
                    { onDialogResponseFailure(it) }
                )
        )
    }

    private fun showAvailableTravelCards(availableTravelCards: ArrayList<TravelCardViewModel>) {
        lastHandableActionNotNeedToBeHandled = true
        removeOnlyLoadingMessage()

        viewState.showRefillTravelCardDialog(availableTravelCards)
    }

    private fun handleChooseAddressFromChatAction(addressFromChatAction: ChooseAddressFromChatAction) {
        Timber.i("handleChooseAddressFromChatAction : $addressFromChatAction")
        if (lastHandableActionIsHandled) {
            Timber.i("chosen address from chat is handled")
            sendChosenPlace(addressFromChatAction.actionId)
        } else {
            Timber.i("chosen address from chat not handled")
            getPlaceById(addressFromChatAction)
        }

    }

    private fun getPlaceById(addressFromChatAction: ChooseAddressFromChatAction) {
        Timber.i("getting Place by id...")
        searchPlaceUseCase.setSessionToken(addressFromChatAction.googleToken)
        addDisposable(
            searchPlaceUseCase.getPlaceByID(
                addressFromChatAction.addressViewModel.placeID,
                addressFromChatAction.addressViewModel.name,
                addressFromChatAction.addressViewModel.description
            )
                .schedulersIoToMain()
                .subscribe(
                    { place ->
                        chatMessages.removeAll { it is LoadingBotMessage }
                        onUserChoosePlace(place)
                    },
                    { onDialogResponseFailure(it) }
                )
        )
    }

    private fun sendChosenPlace(actionId: String) {
        Timber.i("sending chosen place...")
        placeFrom?.let {
            sendChosenActionWithClientLocation(actionId, it)
        } ?: also {
            placeTo?.let {
                sendChosenActionWithClientLocation(actionId, it)
            }
        }
    }

    private fun sendChosenTripTime(actionId: String) {
        Timber.i("sending chosen place...")
        departureTime?.let {
            sendChosenActionWithClientTripTime(actionId, it)
        } ?: also {
            arrivalTime?.let {
                sendChosenActionWithClientTripTime(actionId, it)
            }
        }
    }

    private fun getLastKnownLocation() {
        Timber.i("getting LastKnownLocation...")
        placeFoundDisposable?.dispose()

        placeFoundDisposable = locationManager.getLasLocationObservable()
            .doOnSubscribe { locationManager.requestLastKnownLocation() }
            .subscribe(
                { lastLocationResponse ->
                    when (lastLocationResponse) {
                        is LastLocationEventSuccess -> {
                            onUserLocationReceived(lastLocationResponse.lat, lastLocationResponse.lon)
                        }
                        is LastLocationEventFailure -> {
                            Timber.i("getting last location failure: ${lastLocationResponse.cause}")
                            viewState.showNoLocationError()
                            lastHandableActionNotNeedToBeHandled = true
                            removeOnlyLoadingMessage()
                        }
                    }
                    placeFoundDisposable?.dispose()
                },
                {
                    placeFoundDisposable?.dispose()
                }
            )
    }

    private fun onUserLocationReceived(lat: Double, lon: Double) {
        Timber.i("setting User Choose Place")
        chatMessages.removeAll { it is LoadingBotMessage }
        onUserChoosePlace(Place(lat = lat, lon = lon))
    }

    private fun startSpeechRecognizing() {
        if (speechRecognitionDisposable == null) {
            speechRecognitionDisposable = speechRecognitionManager.observeRecognitionResults()
                .doOnSubscribe { speechRecognitionManager.startRecognize() }
                .subscribe {
                    when {
                        it.isOnNext -> {
                            when (it.value) {
                                is SpeechRecognitionStart -> {
                                    viewState.disableChatSendingButton()
                                    viewState.startRecordAnimating()
                                }
                                is SpeechRecognitionEnd -> {
                                    viewState.enableChatSendingButton()
                                    viewState.stopRecordAnimating()
                                }
                                is SpeechRecognitionData -> {
                                    with((it.value as SpeechRecognitionData).result) {
                                        if (!isBlank()) {
                                            viewState.setUserMessage(this)
                                        }
                                    }
                                }
                            }
                        }
                        it.isOnError -> {
                            viewState.enableChatSendingButton()

                            if (SpeechRecognizer.ERROR_RECOGNIZER_BUSY.toString() == it.error?.message) {
                                speechRecognitionManager.stopRecognize()
                            }
                        }
                        else -> {
                        }
                    }
                }
        } else {
            speechRecognitionManager.startRecognize()
        }
    }

    private fun createActionFromSkillItem(
        chosenItem: SkillsItem
    ): PredefinedUserAction {
        return PredefinedUserAction(
            ActionEntity(
                false, // пока не появится в скилах handable=true action
                chosenItem.skills[0].text,
                chosenItem.skills[0].actionId
            )
        )
    }

    private fun clearChosenPlaces() {
        placeFrom = null
        placeTo = null
    }

    private fun clearChosenTime() {
        arrivalTime = null
        departureTime = null
    }

    private inline fun <reified T : Any> getChatItemIfExists(chatIndex: Int): T? {
        val chatItem = chatMessages.getOrNull(chatIndex) ?: also {
            Timber.e("index $chatIndex is out of bounds chatMessages: $chatMessages")
            return null
        }
        if (chatItem !is T) {
            reportUnexpectedTypeError(T::class, chatItem::class)
            return null
        }
        return chatItem
    }

    private fun getBuildRoute(title: String): PredefinedUserAction {
        return PredefinedUserAction(
            ActionEntity(
                handable = false,
                title = title,
                action_id = ActionIds.BUILD_ROUTE
            )
        )
    }

    private fun checkLongRouteState() {
        addDisposable(actionsUseCase.asyncAvailable()
            .schedulersIoToMain()
            .subscribe(
                { response ->
                    isAvailableLongRoute = response.status ?: false
                    viewState.setLongRouteIconVisibility(isAvailableLongRoute)
                },
                { th ->
                    Timber.e(th, "Failed checkLongRouteState") // по идее никогда не должен вызваться, так как запросы зациклены
                }
            )
        )
    }

    /*@Inject
    lateinit var savedInstanceStateRepository: SavedInstanceStateRepository*/

/*    fun onRestoreInstanceState(restoredInstanceState: Bundle?) {
        Timber.d("Chat restore instance state")
        if (restoredInstanceState != null) {
            val chatRestoredState = savedInstanceStateRepository
                .restoreChatState(restoredInstanceState)

            chatRestoredState?.let {
                wasFirstViewAttach = it.wasFirstViewAttach
                sessionIsCreated = it.sessionIsCreated
                chatMessages.clear()
                chatMessages.addAll(it.chatMessages)
                userActions.clear()
                userActions.addAll(it.userActions)
            }

            Timber.d("Restored chatMessages: $chatMessages")
            Timber.d("Restored userActions: $userActions")
            Timber.d("Restored wasFirstViewAttach: $wasFirstViewAttach")
            Timber.d("Restored sessionIsCreated: $sessionIsCreated")
        }
    }

    fun onSaveInstanceState(bundle: Bundle) {
        Timber.d("Chat save instance state chatMessages: $chatMessages")
        Timber.d("Chat save instance state userActions: $userActions")
        Timber.d("Chat save instance state wasFirstViewAttach: $wasFirstViewAttach")
        Timber.d("Chat save instance state sessionIsCreated: $sessionIsCreated")
        savedInstanceStateRepository.saveChatState(
            bundle,
            SavedInstanceStateRepository.ChatState(chatMessages, userActions, wasFirstViewAttach, sessionIsCreated)
        )
    }*/
}