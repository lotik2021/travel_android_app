package ru.movista.presentation.routedetail.viewholders

import android.view.View
import kotlinx.android.synthetic.main.item_route_detail_destination.view.*
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.viewmodel.DestinationDetailViewModel

class DestinationViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    fun bind(destinationDetailViewModel: DestinationDetailViewModel, previousIsTaxi: Boolean) {
        if (previousIsTaxi) {
            itemView.setGone()
            return
        }
        itemView.destination_address.text = destinationDetailViewModel.addressName
        itemView.destination_time.text = destinationDetailViewModel.time
        itemView.setVisible()
    }
}