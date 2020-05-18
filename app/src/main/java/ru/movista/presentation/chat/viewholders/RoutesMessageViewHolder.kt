package ru.movista.presentation.chat.viewholders

import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item_message_routes.view.*
import ru.movista.presentation.chat.RoutesItem
import ru.movista.presentation.chat.adapters.RoutesAdapter

class RoutesMessageViewHolder(view: View, action: (Int, Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    private val routesAdapter = RoutesAdapter {
        action.invoke(adapterPosition, it)
    }

    init {
        itemView.routes_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            itemView.context,
            LinearLayout.HORIZONTAL,
            false
        )

        itemView.routes_recycler_view.adapter = routesAdapter
    }

    fun bind(routesItem: RoutesItem) {
        routesAdapter.setItems(routesItem.routes)
    }
}