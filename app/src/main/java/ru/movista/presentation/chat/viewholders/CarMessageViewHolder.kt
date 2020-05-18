package ru.movista.presentation.chat.viewholders

import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item_message_car_routes.view.*
import ru.movista.presentation.chat.CarItem
import ru.movista.presentation.chat.adapters.CarRoutesAdapter

class CarMessageViewHolder(view: View, private val action: (Int, Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    init {
        itemView.car_routes_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            itemView.context,
            LinearLayout.HORIZONTAL,
            false
        )
    }

    fun bind(carItem: CarItem) {
        val adapter = CarRoutesAdapter(
            { action.invoke(adapterPosition, it) },
            carItem.carRoutes
        )
        itemView.car_routes_recycler_view.adapter = adapter
    }
}