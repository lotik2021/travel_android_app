package ru.movista.presentation.chat.viewholders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message_routes.view.*
import ru.movista.presentation.chat.LongRoutesItem
import ru.movista.presentation.chat.adapters.LongRoutesAdapter

class LongRouteMessageViewHolder(view: View, action: (Int, Int) -> Unit) : RecyclerView.ViewHolder(view) {

    private val routesAdapter = LongRoutesAdapter {
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

    fun bind(routesItem: LongRoutesItem) {
        routesAdapter.setItems(routesItem.longRoutes)
    }
}