package ru.movista.presentation.chat.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.movista.R
import ru.movista.presentation.chat.*
import ru.movista.presentation.chat.viewholders.*
import ru.movista.presentation.utils.HandlersHolder
import timber.log.Timber

class ChatAdapter(
    private val onTaxiClicked: (Int, Int) -> Unit,
    private val onRouteClicked: (Int, Int) -> Unit,
    private val onCarRouteClicked: (Int, Int) -> Unit,
    private val onMovistaRouteClicked: (Int, Int) -> Unit,
    private val onMovistaPlaceholderClicked: (Int) -> Unit,
    private val onSkillClicked: (Int, Int) -> Unit,
    private val onAddressesClicked: (Int, Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    companion object {
        const val DELAY_FOR_READ = 550L

        private const val NO_DELAY = 0L

        private const val MESSAGE_FROM_BOT = 1
        private const val MESSAGE_FROM_USER = 2
        private const val TAXI_ORDER = 3
        private const val MAP_PREVIEW = 4
        private const val LOADING_MESSAGE_FROM_BOT = 5
        private const val ROUTES = 6
        private const val CAR_ROUTES = 7
        private const val MOVISTA_ROUTES = 8
        private const val MOVISTA_PLACEHOLDER = 9
        private const val SKILL = 10
        private const val ADDRESSES = 11
        private const val WEATHER = 12
    }

    val messages: ArrayList<ChatItem> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        return when (messages[position]) {
            is BotMessage -> MESSAGE_FROM_BOT
            is UserMessage -> MESSAGE_FROM_USER
            is TaxiItem -> TAXI_ORDER
            is MapItem -> MAP_PREVIEW
            is LoadingBotMessage -> LOADING_MESSAGE_FROM_BOT
            is RoutesItem -> ROUTES
            is CarItem -> CAR_ROUTES
            is LongRoutesItem -> MOVISTA_ROUTES
            is MovistaPlaceholderItem -> MOVISTA_PLACEHOLDER
            is SkillsItem -> SKILL
            is AddressesItem -> ADDRESSES
            is WeatherItem -> WEATHER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_FROM_BOT -> {
                BotMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_chat_bot_message, parent, false)
                )
            }
            MAP_PREVIEW -> {
                MapMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_map_preview_message, parent, false)
                )
            }
            TAXI_ORDER -> {
                TaxiMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_message_taxi_routes, parent, false),
                    onTaxiClicked
                )
            }
            MESSAGE_FROM_USER -> UserMessageViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_chat_user_message, parent, false)
            )

            LOADING_MESSAGE_FROM_BOT -> {
                LoadingBotMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_loading_bot_message, parent, false)
                )
            }

            ROUTES -> {
                RoutesMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_message_routes, parent, false),
                    onRouteClicked
                )
            }

            CAR_ROUTES -> {
                CarMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_message_car_routes, parent, false),
                    onCarRouteClicked
                )
            }

            MOVISTA_ROUTES -> {
                LongRouteMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_message_routes, parent, false),
                    onMovistaRouteClicked
                )
            }

            MOVISTA_PLACEHOLDER -> {
                MovistaPlaceholderMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_movista_placeholder, parent, false),
                    onMovistaPlaceholderClicked
                )
            }

            SKILL -> {
                SkillsMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_message_skills, parent, false),
                    onSkillClicked
                )
            }

            ADDRESSES -> {
                AddressesMessageViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_message_addresses, parent, false),
                    onAddressesClicked
                )
            }

            WEATHER -> {
                WeatherViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_weather, parent, false)
                )
            }

            else -> throw IllegalStateException("Undefined ViewType")
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is BotMessageViewHolder -> viewHolder.bind(messages[position] as BotMessage)
            is UserMessageViewHolder -> viewHolder.bind(messages[position] as UserMessage)
            is MapMessageViewHolder -> viewHolder.bind(messages[position] as MapItem)
            is TaxiMessageViewHolder -> viewHolder.bind(messages[position] as TaxiItem)
            is LoadingBotMessageViewHolder -> viewHolder.bind(messages[position] as LoadingBotMessage)
            is RoutesMessageViewHolder -> viewHolder.bind(messages[position] as RoutesItem)
            is CarMessageViewHolder -> viewHolder.bind(messages[position] as CarItem)
            is LongRouteMessageViewHolder -> viewHolder.bind(messages[position] as LongRoutesItem)
            is MovistaPlaceholderMessageViewHolder -> viewHolder.bind(messages[position] as MovistaPlaceholderItem)
            is SkillsMessageViewHolder -> viewHolder.bind(messages[position] as SkillsItem)
            is AddressesMessageViewHolder -> viewHolder.bind(messages[position] as AddressesItem)
            is WeatherViewHolder -> viewHolder.bind(messages[position] as WeatherItem)
        }
    }

    override fun onViewRecycled(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {

        if (holder is MapMessageViewHolder) {
            holder.onRecycle()
        } else {
            super.onViewRecycled(holder)
        }
    }

    fun addItemsOneByOne(items: List<ChatItem>, onItemAddedCallback: () -> Unit) {

        // показываем новые сообщения одно за другим
        items.forEachIndexed { index, chatItem ->

            val delay = computeDelay(index, chatItem, items.size)

            HandlersHolder.chatMessagesHandler.postDelayed(
                {
                    addItem(chatItem)

                    onItemAddedCallback.invoke()
                },
                delay
            )
        }
    }

    fun removeLastItem() {
        messages.removeAt(messages.lastIndex)
        notifyDataSetChanged()
    }

    fun setItems(items: ArrayList<ChatItem>) {
        messages.clear()
        messages.addAll(items)
        notifyDataSetChanged()
    }

    fun clearAll() {
        messages.clear()
        notifyDataSetChanged()
    }

    /**
     * Вычисляем задержку для каждого сообщения, попадающего в чат
     */
    private fun computeDelay(index: Int, chatItem: ChatItem, itemsToAddSize: Int): Long {
        fun computeStandartDelay(): Long {
            return when (index) {
                0 -> {
                    if (messages.isNotEmpty()) {
                        ChatItemsAnimator.ADD_ANIMATION_DURATION
                    } else {
                        NO_DELAY
                    }
                }
                else -> {
                    index * (ChatItemsAnimator.ADD_ANIMATION_DURATION + DELAY_FOR_READ)
                }
            }
        }

        return when (chatItem) {
            is UserMessage -> {
                if (itemsToAddSize > 1) {
                    ChatItemsAnimator.ADD_ANIMATION_DURATION
                } else {
                    NO_DELAY // сообщение от пользователя показываем без задержки, если оно единственное из списка для показа
                }
            }
            is LoadingBotMessage -> {
                // загрузку сообщения показываем с задержкой, если это не начало диалога
                if (messages.isNotEmpty()) {
                    if (itemsToAddSize > 1) {
                        // если сообщение о загрузке не единственное для показа - отображаем со стандартной задержкой списка
                        computeStandartDelay()
                    } else {
                        ChatItemsAnimator.ADD_ANIMATION_DURATION + DELAY_FOR_READ
                    }
                } else {
                    NO_DELAY
                }
            }
            else -> computeStandartDelay()
        }
    }

    private fun addItem(chatItem: ChatItem) {
        if (messages.isNotEmpty()) {
            if (messages.last() is LoadingBotMessage) {
                // удаляем загрузку сообщения, на его место добавляем ответ
                messages.removeAt(messages.lastIndex)
                messages.add(chatItem)
                notifyItemChanged(messages.lastIndex)
            } else {
                messages.add(chatItem)
                notifyItemInserted(messages.lastIndex)
            }
        } else {
            messages.add(chatItem)
            notifyItemInserted(messages.lastIndex)
        }

        // изменяем предпоследний элемент, чтобы корректо отобразились отступы MarginItemDecoration
        // добавляем Any() аргумент, чтобы к предпоследнему элементу не применялась анимация
        // https://stackoverflow.com/a/47355363/7488771
        if (messages.lastIndex != 0) {
            notifyItemChanged(messages.lastIndex - 1, Any())
        }
        Timber.i("View: item added: $chatItem")
    }


    class ChatMarginItemDecoration(private val bottomMargin: Int) :
        androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: androidx.recyclerview.widget.RecyclerView,
            state: androidx.recyclerview.widget.RecyclerView.State
        ) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0
            }

            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                outRect.bottom = bottomMargin
            }
        }
    }

    class ChatItemsAnimator : androidx.recyclerview.widget.DefaultItemAnimator() {

        companion object {
            const val ADD_ANIMATION_DURATION = 300L
            const val CHANGE_ANIMATION_DURATION = 300L
        }

        init {
            this.addDuration = ADD_ANIMATION_DURATION
            this.changeDuration = CHANGE_ANIMATION_DURATION
        }
    }
}