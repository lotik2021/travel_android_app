package ru.movista.presentation.chat.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message_taxi_routes.view.*
import ru.movista.presentation.chat.TaxiItem
import ru.movista.presentation.chat.adapters.TaxiRoutesAdapter

class TaxiMessageViewHolder(
    view: View, action: (Int, Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val taxiRoutesAdapter = TaxiRoutesAdapter {
        action.invoke(adapterPosition, it)
    }

    init {
        itemView.taxi_routes_recycler_view.layoutManager = LinearLayoutManager(
            itemView.context,
            RecyclerView.HORIZONTAL,
            false
        )

        itemView.taxi_routes_recycler_view.adapter = taxiRoutesAdapter
    }


    fun bind(taxiItem: TaxiItem) {
        taxiRoutesAdapter.setItems(taxiItem.taxiProviders)
    }
}