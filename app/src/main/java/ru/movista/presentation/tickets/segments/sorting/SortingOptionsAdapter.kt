package ru.movista.presentation.tickets.segments.sorting

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_sorting_options.view.*
import ru.movista.R
import ru.movista.data.source.local.models.TicketSortingOption
import ru.movista.utils.inflate

class SortingOptionsAdapter(
    private val items: List<TicketSortingOption>,
    private var selectedItem: TicketSortingOption,
    private val action: (TicketSortingOption) -> Unit
) : RecyclerView.Adapter<SortingOptionsAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(parent.context.inflate(R.layout.item_sorting_options, parent))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        with(holder.itemView.option) {
            val optionItem = items[position]
            option.setText(optionItem.titleResId)
            holder.removeCheckedListener()

            if (optionItem == selectedItem) {
                option.typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
                option.isChecked = true
            } else {
                option.typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
                option.isChecked = false
            }

            holder.setCheckedListener(optionItem)
        }
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setCheckedListener(sortingOption: TicketSortingOption) {
            itemView.option.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItem = sortingOption
                }
                notifyDataSetChanged()
                action(selectedItem)
            }
        }

        fun removeCheckedListener() {
            itemView.option.setOnCheckedChangeListener(null)
        }
    }
}