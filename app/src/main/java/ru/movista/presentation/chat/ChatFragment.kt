package ru.movista.presentation.chat

import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.content.Context
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_chat.*
import moxy.MvpView
import moxy.presenter.InjectPresenter
import org.jetbrains.anko.support.v4.toast
import org.threeten.bp.LocalDateTime
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.carnavigation.CarNavigationDialogFragment
import ru.movista.presentation.chat.adapters.ChatAdapter
import ru.movista.presentation.chat.adapters.UserMessagesAdapter
import ru.movista.presentation.custom.DateTimePicker
import ru.movista.presentation.main.MainActivity
import ru.movista.presentation.refilltravelcard.RefillTravelCardDialogFragment
import ru.movista.presentation.refilltravelcard.TravelCardViewModel
import ru.movista.presentation.taxiorder.TaxiOrderDialogFragment
import ru.movista.presentation.utils.*
import ru.movista.presentation.viewmodel.CarRouteViewModel
import ru.movista.presentation.viewmodel.PredefinedUserAction
import ru.movista.presentation.viewmodel.TaxiProviderViewModel
import ru.movista.utils.EMPTY
import ru.movista.utils.reportNullFieldError
import timber.log.Timber

class ChatFragment : BaseFragment(), ChatView {
    companion object {
        const val TAG_REFILL_TRAVEL_CARD_BOTTOM_DIALOG = "refill_travel_card"
        private const val TAG_TAXI_ORDER_BOTTOM_DIALOG = "taxi_order"
        private const val TAG_CAR_NAVIGATION_BOTTOM_DIALOG = "car_navigation"
    }

    private var chatAnimator: ChatAdapter.ChatItemsAnimator? = null

    private var chatAdapter: ChatAdapter? = null
    private var userMessagesAdapter: UserMessagesAdapter? = null
    private var userMessagesLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private var chatLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private var lastChatItemPosition = RecyclerView.NO_POSITION

    private var recordDecorationAnimator: ValueAnimator? = null

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    override fun sharePresenter(): BasePresenter<MvpView>? {
        return presenter as? BasePresenter<MvpView>
    }

    override fun getLayoutRes() = R.layout.fragment_chat

    override fun onFragmentInject() {
        Injector.init(Screens.Chat.TAG)
        Injector.chatComponent?.inject(this)
    }

    override fun afterCreate() {
        super.afterCreate()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun initUI() {
        super.initUI()
        // анимация увеличения высоты
        chat_content.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        setupChatRecyclerView()

        setupUserActionsRecyclerView()

        disable_click_layout.setOnClickListener { /*disable any click*/ }

        chat_toolbar.setNavigationOnClickListener { presenter.onLongRouteScreenClick() }

        TooltipCompat.setTooltipText(profile, getString(R.string.profile))
        profile.setOnClickListener { presenter.onAppInfoClick() }

        chat_user_message_send.setOnClickListener {
            presenter.onUserMessageSendClicked(chat_user_message_input.text.toString())
            scrollChatToTheLastPosition()
        }

        chat_user_message_input.onTextChangedListener { chars ->
            chat_user_message_send?.isEnabled = !chars.isNullOrEmpty()
            chat_user_message_send?.isClickable = !chars.isNullOrEmpty()
        }

        chat_enter_message.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                // появилась клавиатура
                if (isScrolledToEndChat()) {
                    scrollChatToTheLastPosition(true)
                }

                chat_user_message_input?.post { chat_user_message_input?.requestFocus() }
            }
        }

        chat_refresh_button.setOnClickListener { presenter.onChatRefreshClicked() }

        chat_user_message_record.setOnClickListener { presenter.onRecognizeSpeechClicked() }

        move_to_bottom.setOnClickListener { scrollChatToTheLastPosition(true) }
    }

    override fun setLongRouteIconVisibility(isAvailableLongRoute: Boolean) {
        if (isAvailableLongRoute) {
            chat_toolbar.setNavigationIcon(R.drawable.ic_long_routes)
        } else {
            chat_toolbar.navigationIcon = null
        }
    }

    override fun showDatePicker(
        isShowTime: Boolean,
        dataSetListener: (LocalDateTime) -> Unit,
        cancelListener: (() -> Unit)
    ) {
        view?.post { DateTimePicker(requireContext(), dataSetListener, cancelListener, isShowTime).show() }
    }

    override fun enableChatSendingButton() {
        if (!chat_user_message_input.text.isNullOrEmpty()) {
            chat_user_message_send.isEnabled = true
            chat_user_message_send?.isClickable = true
        }
    }

    override fun disableChatSendingButton() {
        chat_user_message_send.isEnabled = false
        chat_user_message_send?.isClickable = false
    }

    override fun startRecordAnimating() {
        chat_user_message_record.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
        record_decorator.setVisible()

        val marginEnd = resources.getDimensionPixelOffset(R.dimen.record_decorator_margin_end).toFloat()
        val marginStart = resources.getDimensionPixelOffset(R.dimen.record_decorator_margin_start)
        val marginOffset = marginStart / marginEnd

        recordDecorationAnimator = ValueAnimator.ofFloat(0f, marginEnd).apply {
            duration = 750
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                active_record_decorator?.let {
                    val fractionAnim = animator.animatedValue as Float

                    val lp = it.layoutParams as FrameLayout.LayoutParams
                    lp.setMargins((fractionAnim.toInt() * marginOffset).toInt(), 0, fractionAnim.toInt(), 0)
                    it.layoutParams = lp
                }
            }

            start()
        }
    }

    override fun stopRecordAnimating() {
        record_decorator.setInVisible()
        chat_user_message_record.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue))
        recordDecorationAnimator?.cancel()
    }

    override fun clearUI() {
        super.clearUI()
        recordDecorationAnimator?.cancel()
        chat_recycler_view.removeOnScrollListener(chatScrollListener)
        chat_recycler_view.adapter = null
        chat_user_actions_recycler_view.adapter = null
        chatLayoutManager = null
        userMessagesLayoutManager = null
        chatAnimator = null
    }

    override fun showNewMessages(chatItems: List<ChatItem>) {

        // смотрим сколько есть сообщений (не загрузок)
        val currentMessages = chatAdapter?.messages?.filter { it !is LoadingBotMessage } ?: return
        val startCount = currentMessages.size
        val endCount = startCount + chatItems.size

        val doAfterItemAdded: () -> Unit = {
            chatAdapter?.let {

                if (chatItems.isNotEmpty() && getChatHiddenMessageCount() > 1) {
                    new_message_notification.setVisible()
                    move_to_bottom.show()
                } else {
                    scrollChatToTheLastPosition()
                }

                if (it.itemCount == endCount) {
                    HandlersHolder.chatMessagesHandler.postDelayed(
                        {
                            presenter.onAllMessagesShown(chatItems)
                        },
                        ChatAdapter.DELAY_FOR_READ
                    )
                }
            } ?: reportNullFieldError("chatAdapter")
        }

        chatAdapter?.addItemsOneByOne(chatItems) { doAfterItemAdded.invoke() }
    }

    override fun deleteLastMessage(previous: List<ChatItem>) {
        chatAdapter?.removeLastItem()
        presenter.onAllMessagesShown(previous)
    }

    override fun showAllMessages(chatItems: ArrayList<ChatItem>) {
        chatAdapter?.setItems(chatItems)
        presenter.onAllMessagesShown(chatItems)
    }

    override fun showUserMessagesChoice(messages: ArrayList<PredefinedUserAction>) {

        userMessagesAdapter?.messages = messages
        userMessagesAdapter?.makeInactive = false

        val params = chat_user_actions.layoutParams
        params.height = resources.getDimensionPixelSize(R.dimen.е64)
        chat_user_actions.layoutParams = params

        // скролим чат до конца, если юзер не проскоролил чат выше
        chatAdapter?.let { adapter ->
            val lastItemPosition = adapter.itemCount - 1
            chatLayoutManager?.let {
                val lastVisiblePosition = it.findLastCompletelyVisibleItemPosition()
                if (lastItemPosition == lastVisiblePosition) scrollChatToTheLastPosition()
            }
        }

        userMessagesLayoutManager?.scrollToPosition(0)
    }

    override fun hideUserMessagesChoices() {
        userMessagesAdapter?.makeInactive = true
        userMessagesAdapter?.notifyDataSetChanged()
    }

    override fun showTaxiOrderDialog(taxiProviderViewModel: TaxiProviderViewModel) {

        val taxiOrderDialogFragment = TaxiOrderDialogFragment
            .getInstance(taxiProviderViewModel)

        taxiOrderDialogFragment.show(childFragmentManager, TAG_TAXI_ORDER_BOTTOM_DIALOG)

    }

    override fun showRefillTravelCardDialog(travelCardViewModel: ArrayList<TravelCardViewModel>) {
        val refillTravelCardDialogFragment =
            RefillTravelCardDialogFragment.newInstance(travelCardViewModel)

        refillTravelCardDialogFragment.show(childFragmentManager, TAG_REFILL_TRAVEL_CARD_BOTTOM_DIALOG)
    }

    override fun showCarRouteInfoDialog(carRouteViewModel: CarRouteViewModel) {
        val carNavigationDialogFragment = CarNavigationDialogFragment.getInstance(carRouteViewModel)
        carNavigationDialogFragment.show(childFragmentManager, TAG_CAR_NAVIGATION_BOTTOM_DIALOG)
    }

    override fun disableAnyClicks() {
        Timber.i("view: disableAnyClicks")
        disable_click_layout.setVisible()
    }

    override fun enableAnyClicks() {
        Timber.i("view: enableAnyClicks")
        disable_click_layout.setGone()
    }

    override fun requestLocationPermission() {
        (activity as? MainActivity)?.requestLocationPermission()
    }

    override fun requestAudioPermission() {
        (activity as? MainActivity)?.requestAudioPermission()
    }

    override fun openWebLink(url: String) {
        (activity as? Context)?.openInCustomBrowser(url)
    }

    override fun changeInputHint(hint: String) {
        chat_user_message_input.hint = hint
    }

    override fun hideKeyboard() {
        chat_user_message_input.hideSoftKeyboard()
    }

    override fun setUserMessage(message: String) {
        chat_user_message_input.setText(message)
        chat_user_message_input.setSelection(message.length)
    }

    override fun showStartDialogError() {
        chat_enter_message.setGone()
        chat_recycler_view.setGone()
        chat_no_connection.setVisible()
    }

    override fun hideStartDialogError() {
        chat_enter_message.setVisible()
        chat_recycler_view.setVisible()
        chat_no_connection.setGone()
    }

    override fun clearUserMessageField() {
        chat_user_message_input.setText(String.EMPTY)
    }

    override fun showNoLocationError() {
        toast(R.string.error_no_location)
    }

    override fun clearChat() {
        chatAdapter?.clearAll()
    }

    override fun removeHistoryAction() {
        userMessagesAdapter?.removeHistoryAction()
    }

    private fun setupUserActionsRecyclerView() {
        userMessagesAdapter = UserMessagesAdapter {
            presenter.onUserMessageChoiceClicked(it)
            scrollChatToTheLastPosition()
        }

        userMessagesLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            activity,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        chat_user_actions_recycler_view.layoutManager = userMessagesLayoutManager
        chat_user_actions_recycler_view.adapter = userMessagesAdapter

        chat_user_actions_recycler_view.addItemDecoration(
            UserMessagesAdapter.UserMessageMarginItemDecorator(
                resources.getDimensionPixelOffset(R.dimen.size_small),
                resources.getDimensionPixelOffset(R.dimen.size_small_plus)
            )
        )
    }

    private fun setupChatRecyclerView() {
        chatAdapter = ChatAdapter(
            { chatIndex, taxiItemIndex -> presenter.onTaxiItemClicked(chatIndex, taxiItemIndex) },
            { chatIndex, routeItemIndex -> presenter.onRouteItemClicked(chatIndex, routeItemIndex) },
            { chatIndex, carRouteIndex -> presenter.onCarRouteClicked(chatIndex, carRouteIndex) },
            { chatIndex, longRouteIndex -> presenter.onLongRouteItemClicked(chatIndex, longRouteIndex) },
            { chatIndex -> presenter.onMovistaPlaceholderClicked(chatIndex) },
            { chatIndex, skillIndex -> presenter.onSkillMessageClicked(chatIndex, skillIndex) },
            { chatIndex, addressIndex -> presenter.onAddressClicked(chatIndex, addressIndex) }
        )

        chatLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        chat_recycler_view.layoutManager = chatLayoutManager
        chat_recycler_view.adapter = chatAdapter

        chatAnimator = ChatAdapter.ChatItemsAnimator()

        chat_recycler_view.itemAnimator = chatAnimator
        chat_recycler_view.addItemDecoration(
            ChatAdapter.ChatMarginItemDecoration(resources.getDimensionPixelOffset(R.dimen.size_xlarge))
        )

        chat_recycler_view.addOnScrollListener(chatScrollListener)
    }

    private val chatScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            // делаем в post, так как нужно чтобы событие скролла сработала после появления клавиатуры
            // callback клавиатуры срабатывает при изменение элемента chat_enter_message
            chat_enter_message.post {
                val itemPosition = chatLayoutManager?.findLastCompletelyVisibleItemPosition() ?: -1
                lastChatItemPosition = itemPosition

                if (getChatHiddenMessageCount() > 1) {
                    move_to_bottom?.show() ?: reportNullFieldError("move_to_bottom fab")
                } else {
                    move_to_bottom?.hide() ?: reportNullFieldError("move_to_bottom fab")
                    new_message_notification?.setGone() ?: reportNullFieldError("new_message_notification")
                }
            }
        }
    }

    private fun isScrolledToEndChat(isAddedNewItem: Boolean = false): Boolean {
        return if (isAddedNewItem) {
            (
                    chatLayoutManager?.findFirstVisibleItemPosition() == RecyclerView.NO_POSITION
                    || lastChatItemPosition == chatLayoutManager?.itemCount?.minus(1)
                            || lastChatItemPosition == chatLayoutManager?.itemCount?.minus(2)
                    )
        } else {
            (
                    chatLayoutManager?.findFirstVisibleItemPosition() == RecyclerView.NO_POSITION
                            || lastChatItemPosition == chatLayoutManager?.itemCount?.minus(1)
                    )
        }
    }

    /**
     * Возвращает количество скрытых (проскроленных) элементов чата
     */
    private fun getChatHiddenMessageCount(): Int {
        chatLayoutManager?.let { layoutManager ->
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition() + 1

            if (lastVisibleItem != RecyclerView.NO_POSITION) {
                return layoutManager.itemCount - lastVisibleItem
            }
        }
        return 0
    }

    private fun scrollChatToTheLastPosition(withAnimation: Boolean = false) {
        chatAdapter?.let {
            if (it.itemCount < 1) return@let // на всякий случай

            if (!withAnimation) {
                chatLayoutManager?.scrollToPosition(it.itemCount - 1)
            } else {
                chat_recycler_view.smoothScrollToPosition(it.itemCount - 1)
            }
        } ?: reportNullFieldError("chatAdapter")
    }
}