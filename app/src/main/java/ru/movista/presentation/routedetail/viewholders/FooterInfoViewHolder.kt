package ru.movista.presentation.routedetail.viewholders

import android.view.View
import kotlinx.android.synthetic.main.item_route_detail_info.view.*

class FooterInfoViewHolder(view: View, clickListener: () -> Unit) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    init {
        view.route_detail_info.setOnClickListener {
            clickListener.invoke()
        }
    }
}