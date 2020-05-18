package ru.movista.presentation.tickets.basket

import android.graphics.PorterDuff
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.basket_item.view.*
import kotlinx.android.synthetic.main.search_hint_item.view.*
import ru.movista.R
import ru.movista.data.source.local.models.BasketItemState
import ru.movista.data.source.local.models.TripType
import ru.movista.presentation.utils.*
import ru.movista.presentation.viewmodel.SegmentViewModel
import ru.movista.utils.EMPTY
import ru.movista.utils.getLowerCaseString

class BasketAdapter(
    private var isShowSearchHint: Boolean,
    private val actions: Actions
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val HINT_VIEW_TYPE = 1
        private const val ITEM_VIEW_TYPE = 2
    }

    private val items = mutableListOf<SegmentViewModel>()
    private var forthDateTitle: String = String.EMPTY
    private var backDateTitle: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        with(LayoutInflater.from(parent.context)) {
            return when (viewType) {
                HINT_VIEW_TYPE -> HintHolder(inflate(R.layout.search_hint_item, parent, false))
                else -> ItemHolder(inflate(R.layout.basket_item, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return when {
            items.isEmpty() -> 0
            else -> items.size + getSearchHintCount()
        }
    }

    override fun getItemViewType(position: Int): Int {
//        return when {
//            isShowSearchHint && position == 0 -> HINT_VIEW_TYPE
//            else -> ITEM_VIEW_TYPE
//        }
        return ITEM_VIEW_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemHolder -> {
                val actualPosition = position - getSearchHintCount()
                val item = items[actualPosition]

                val isShowForthDate = actualPosition == 0
                val isShowReturnDate = isFirstReturnTripItem(position)
                val isEnabledTrip = isEnabledTrip(position, item.tripTypes)

                holder.bind(item, isShowForthDate, isShowReturnDate, isEnabledTrip)
            }
        }
    }

    private fun isEnabledTrip(position: Int, tripTypes: List<TripType>): Boolean {
        val actualPosition = position - getSearchHintCount()
        return !tripTypes.isOnlyTaxi() && (actualPosition == 0 || items[actualPosition - 1].basketItemState == BasketItemState.SELECTED)
    }

    private fun isFirstReturnTripItem(position: Int): Boolean {
        val actualPosition = position - getSearchHintCount()
        val item = items[actualPosition]
        return item.isReturnTrip && items.size > 1 && !items[actualPosition - 1].isReturnTrip
    }

    fun setDate(forthDateTitle: String, backDateTitle: String?) {
        this.forthDateTitle = forthDateTitle
        this.backDateTitle = backDateTitle
    }

    fun updateItems(result: List<SegmentViewModel>) {
        items.clear()
        items.addAll(result)
        notifyDataSetChanged()
    }

    fun removeBasketHint() {
        isShowSearchHint = false
        notifyDataSetChanged()
    }

    private fun getSearchHintCount(): Int = /*if (isShowSearchHint) 1 else 0*/ 0

    private fun List<TripType>.isOnlyTaxi() : Boolean {
        return this.size == 1 && this.first() == TripType.TAXI
    }

    private inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            item: SegmentViewModel,
            isShowForthDate: Boolean,
            isShowReturnDate: Boolean,
            enabledTrip: Boolean
        ) {
            with(itemView) {
                when {
                    isShowForthDate -> {
                        direction_type.setSmallCaps(forthDateTitle)
                        direction_type.setVisible()
                    }
                    isShowReturnDate -> backDateTitle?.let {
                        direction_type.setSmallCaps(it)
                        direction_type.setVisible()
                    }
                    else -> direction_type.setGone()
                }

                title.setSmallCaps(item.title)
                description.text = item.description

                if (enabledTrip) {
                    basket_item_content.isEnabled = true
                    basket_item_content.alpha = 1f
                } else {
                    basket_item_content.isEnabled = false
                    basket_item_content.alpha = 0.5f
                }

                basket_item_content.setScaleClick {
                    if (adapterPosition != NO_POSITION) {
                        actions.onItemClick(items[adapterPosition - getSearchHintCount()])
                    }
                }

                menu_image.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        actions.onOptionItemClick(items[adapterPosition - getSearchHintCount()])
                    }
                }

                when {
                    item.tripTypes.isOnlyTaxi() -> {
                        sub_description.setGone()
                        menu_image.setInVisible()
                        right_arrow.setInVisible()
                        setTransportTypeImage(item)
                    }
                    item.basketItemState == BasketItemState.SELECTED -> {
                        sub_description.text = item.subDescription
                        sub_description.setVisible()
                        menu_image.setVisible()
                        right_arrow.setInVisible()
                        setTransportTypeImage(item)
                    }
                    item.basketItemState == BasketItemState.NOT_SELECTED -> {
                        sub_description.setGone()
                        menu_image.setInVisible()
                        right_arrow.setVisible()
                        setTransportTypeImage(item)
                    }
                }
            }
        }

        private fun setTransportTypeImage(item: SegmentViewModel) {
            with(itemView) {
                val tripSegmentIcons = item.tripTypes

                transport_type_image.removeAllViews()
                transport_type_image.setSingleGroupItems(tripSegmentIcons) { image, tripSegmentIcon ->
                    image.setImageResource(R.drawable.ic_ellipse)

                    val colorResId: Int = when (tripSegmentIcon) {
                        TripType.TRAIN -> R.color.primary_train
                        TripType.FLIGHT -> R.color.primary_flight
                        TripType.BUS -> R.color.primary_bus
                        TripType.TRAIN_SUBURBAN -> R.color.primary_suburban
                        TripType.TAXI -> R.color.primary_taxi
                        else -> R.color.blue_primary
                    }

                    image.setColorFilter(
                        ContextCompat.getColor(context, colorResId),
                        PorterDuff.Mode.SRC_IN
                    )
                }

                transport_type_image.setVisibility(transport_type_image.childCount != 0)
            }
        }
    }

    private inner class HintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            with(itemView) {
                not_show_title.setOnClickListener { actions.onNotShowBasketHint() }

                val notShowTitleText = context.getLowerCaseString(R.string.not_show_info)
                val underlineSpannable = SpannableString(notShowTitleText)
                underlineSpannable.setSpan(UnderlineSpan(), 0, underlineSpannable.length, 0)
                not_show_title.text = underlineSpannable

                search_info_title.setSmallCaps(context.getString(R.string.tickets_selection))

                search_info_title.setOnClickListener {
                    if (search_info_description.isVisible()) {
                        search_info_description.setGone()
                        not_show_title.setGone()
                        search_info_title.setDrawables(right = R.drawable.ic_bottom_arrow)
                    } else {
                        search_info_description.setVisible()
                        not_show_title.setVisible()
                        search_info_title.setDrawables(right = R.drawable.ic_top_arrow)
                    }
                }
            }
        }
    }

    interface Actions {
        fun onOptionItemClick(item: SegmentViewModel)
        fun onItemClick(item: SegmentViewModel)
        fun onNotShowBasketHint()
    }
}