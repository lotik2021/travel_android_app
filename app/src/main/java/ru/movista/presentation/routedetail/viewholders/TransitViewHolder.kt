package ru.movista.presentation.routedetail.viewholders

import android.view.View
import kotlinx.android.synthetic.main.item_route_detail_transit.view.*
import ru.movista.R
import ru.movista.presentation.viewmodel.TransitViewModel

class TransitViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    fun bind(transitViewModel: TransitViewModel) {
        val resources = itemView.context.resources

        itemView.transit_duration.text = resources.getString(
            R.string.duration_braces,
            transitViewModel.duration
        )
    }
}