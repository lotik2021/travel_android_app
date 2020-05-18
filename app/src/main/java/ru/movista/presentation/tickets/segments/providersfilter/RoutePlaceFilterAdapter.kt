package ru.movista.presentation.tickets.segments.providersfilter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.item_filter_places.view.*
import kotlinx.android.synthetic.main.item_filter_places_header.view.*
import ru.movista.R
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisibility

class RoutePlaceFilterAdapter(
    private val filterChangedListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val ITEM_VIEW_TYPE = 0
        private const val TITLE_VIEW_TYPE = 1
    }

    private val items = mutableListOf<BaseFilterViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TITLE_VIEW_TYPE -> TitleHolder(
                inflater.inflate(
                    R.layout.item_filter_places_header,
                    parent,
                    false
                )
            )
            else -> ItemHolder(inflater.inflate(R.layout.item_filter_places, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TitleFilterViewModel -> TITLE_VIEW_TYPE
            else -> ITEM_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]) {
            is TitleFilterViewModel -> {
                with(holder as TitleHolder) {
                    itemView.title.text = item.title

                    checkedSafeRun {
                        itemView.all_checkbox.isChecked = item.isSelected
                        itemView.reset.setVisibility(!item.isSelected)
                    }
                }
            }
            is ItemFilterViewModel -> {
                with(holder as ItemHolder) {
                    itemView.item_name.text = item.itemPlaceFilter.name

                    val isShowDivider = position == itemCount - 1 || items[position + 1] is TitleFilterViewModel
                    itemView.item_filter_divider.setVisibility(!isShowDivider)

                    checkedSafeRun {
                        itemView.item_checkbox.isChecked = item.itemPlaceFilter.isSelected
                    }
                }
            }
        }
    }

    fun update(filterItems: List<BaseFilterViewModel>) {
        items.clear()
        items.addAll(filterItems)
        notifyDataSetChanged()
    }

    inner class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            with(itemView) {
                filter_header_root.setOnClickListener { all_checkbox.toggle() }

                reset.setOnClickListener {
                    if (adapterPosition == NO_POSITION) {
                        return@setOnClickListener
                    }

                    reset.setGone()

                    val item = items[adapterPosition]

                    if (item !is TitleFilterViewModel) {
                        return@setOnClickListener
                    }

                    items
                        .filter { it.tripType == item.tripType }
                        .forEach {
                            if (it is ItemFilterViewModel) {
                                it.itemPlaceFilter.isSelected = true
                            } else if (it is TitleFilterViewModel) {
                                it.isSelected = true
                            }
                        }

                    val firstIndex = items.indexOf(items.first { it.tripType == item.tripType })
                    val itemsCount = items.filter { (it.tripType == item.tripType) }.count()

                    notifyItemRangeChanged(firstIndex, itemsCount)
                    filterChangedListener.invoke()
                }
            }
        }

        private val allCheckBoxCheckedListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (adapterPosition == NO_POSITION) {
                return@OnCheckedChangeListener
            }

            val item = items[adapterPosition]
            if (item !is TitleFilterViewModel) {
                return@OnCheckedChangeListener
            }

            itemView.reset.setVisibility(!isChecked)

            items
                .filter { (it is ItemFilterViewModel) && (it.tripType == item.tripType) }
                .map { it as ItemFilterViewModel }
                .forEach { it.itemPlaceFilter.isSelected = isChecked }

            val firstIndex = items.indexOf(
                items.first { (it is ItemFilterViewModel) && (it.tripType == item.tripType) }
            )
            val itemsCount = items
                .filter {
                    (it is ItemFilterViewModel) && (it.tripType == item.tripType)
                }
                .count()

            notifyItemRangeChanged(firstIndex, itemsCount)
            filterChangedListener.invoke()
        }

        fun checkedSafeRun(action: () -> Unit) {
            setEnabledAllCheckboxListener(false)
            action.invoke()
            setEnabledAllCheckboxListener(true)
        }

        private fun setEnabledAllCheckboxListener(enabled: Boolean) {
            if (enabled) {
                itemView.all_checkbox.setOnCheckedChangeListener(allCheckBoxCheckedListener)
            } else {
                itemView.all_checkbox.setOnCheckedChangeListener(null)
            }
        }
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            with(itemView) {
                filter_item_root.setOnClickListener { item_checkbox.toggle() }
            }
        }

        private val itemCheckBoxCheckedListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (adapterPosition == NO_POSITION) {
                return@OnCheckedChangeListener
            }

            val item = items[adapterPosition]
            if (item !is ItemFilterViewModel) {
                return@OnCheckedChangeListener
            }

            item.itemPlaceFilter.isSelected = isChecked

            val isSelectedAllFilters = items
                .filter { (it is ItemFilterViewModel) && (it.tripType == item.tripType) }
                .map { it as ItemFilterViewModel }
                .all { it.itemPlaceFilter.isSelected }

            val titleItem = items.first {
                (it is TitleFilterViewModel) && (it.tripType == item.tripType)
            } as TitleFilterViewModel

            titleItem.isSelected = isSelectedAllFilters

            notifyItemChanged(items.indexOf(titleItem))
            filterChangedListener.invoke()
        }

        fun checkedSafeRun(action: () -> Unit) {
            setEnabledItemCheckboxListener(false)
            action.invoke()
            setEnabledItemCheckboxListener(true)
        }

        private fun setEnabledItemCheckboxListener(enabled: Boolean) {
            if (enabled) {
                itemView.item_checkbox.setOnCheckedChangeListener(itemCheckBoxCheckedListener)
            } else {
                itemView.item_checkbox.setOnCheckedChangeListener(null)
            }
        }
    }
}