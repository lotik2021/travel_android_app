package ru.movista.presentation.tickets.tripgroup

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_hint_item.view.*
import kotlinx.android.synthetic.main.search_result_item.view.*
import kotlinx.android.synthetic.main.search_result_mock_item.view.*
import ru.movista.R
import ru.movista.data.source.local.models.PathGroupState
import ru.movista.domain.model.tickets.TripGroup
import ru.movista.presentation.utils.*
import ru.movista.utils.getLowerCaseString
import ru.movista.utils.inflate
import ru.movista.utils.toPriceFormat

class TripGroupAdapter(
    private var isShowSearchHint: Boolean,
    private val searchResultActions: SearchResultActions
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val ITEM_VIEW_TYPE = 0
        private const val MOCK_VIEW_TYPE = 1
        private const val HINT_VIEW_TYPE = 2
        private const val MOCK_COUNT = 5
    }

    private val items = mutableListOf<TripGroup>()
    private var isCompletedSearch: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MOCK_VIEW_TYPE ->
                MockHolder(parent.context.inflate(R.layout.search_result_mock_item, parent))
            HINT_VIEW_TYPE ->
                SearchHintHolder(parent.context.inflate(R.layout.search_hint_item, parent))
            else ->
                ItemSearchHolder(parent.context.inflate(R.layout.search_result_item, parent))
        }
    }

    override fun getItemCount(): Int {
        return if (isCompletedSearch) {
            if (items.isEmpty()) 0 else items.size + getSearchHintCount()
        } else {
            if (items.isEmpty()) {
                MOCK_COUNT
            } else {
                (if (items.size > MOCK_COUNT) items.size else MOCK_COUNT) + getSearchHintCount()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            items.isNotEmpty() && isShowSearchHint && position == 0 -> HINT_VIEW_TYPE
            isCompletedSearch || (items.isNotEmpty() && position - getSearchHintCount() < items.size) -> ITEM_VIEW_TYPE
            else -> MOCK_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            when (holder) {
                is MockHolder -> this.shimmer_layout.startShimmerAnimation()
                is ItemSearchHolder -> {
                    val item = items[position - getSearchHintCount()]
                    title.setSmallCaps(item.title ?: "")
                    routs_description.text = item.description
                    duration.text = context.getString(
                        R.string.trip_duration,
                        item.minDurationTitle ?: ""
                    )

                    val minPrice = item.minPrice ?: 0.0
                    price.setSmallCaps(context.getString(R.string.from_what, minPrice.toPriceFormat()))
                    routs_icons.setItems(item.tripTypeSequenceIcons)

                    if (item.state == PathGroupState.COMPLETED || isCompletedSearch) {
                        price.setVisible()
                        price_loading.setGone()
                        holder.setupClick()
                    } else {
                        price.setInVisible()
                        price_loading.setVisible()
                    }
                }
            }
        }
    }

    fun updateItems(result: List<TripGroup>, completedSearch: Boolean) {
        items.clear()
        items.addAll(result)
        isCompletedSearch = completedSearch
        notifyDataSetChanged()
    }

    fun restartAdapter() {
        items.clear()
        isCompletedSearch = false
        notifyDataSetChanged()
    }

    fun removeSearchHint() {
        isShowSearchHint = false
        notifyDataSetChanged()
    }

    private fun getSearchHintCount(): Int = if (isShowSearchHint) 1 else 0

    private class MockHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private inner class ItemSearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setupClick() {
            itemView.group_content.setScaleClick {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    searchResultActions.onGroupClick(items[adapterPosition - getSearchHintCount()])
                }
            }
        }
    }

    private inner class SearchHintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            with(itemView) {
                not_show_title.setOnClickListener { searchResultActions.onNotShowSearchHintClick() }

                val notShowTitleText = context.getLowerCaseString(R.string.not_show_info)
                val underlineSpannable = SpannableString(notShowTitleText)
                underlineSpannable.setSpan(UnderlineSpan(), 0, underlineSpannable.length, 0)
                not_show_title.text = underlineSpannable

                search_info_title.setSmallCaps(context.getString(R.string.search_info_title))

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

    interface SearchResultActions {
        fun onGroupClick(tripGroup: TripGroup)
        fun onNotShowSearchHintClick()
    }
}