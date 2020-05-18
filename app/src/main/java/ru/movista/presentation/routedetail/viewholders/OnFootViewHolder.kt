package ru.movista.presentation.routedetail.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_route_detail_on_foot.view.*
import ru.movista.R
import ru.movista.presentation.viewmodel.OnFootViewModel

class OnFootViewHolder(view: View, onViewOnMapClickListener: (Int) -> Unit) : RecyclerView.ViewHolder(view) {

    init {
        itemView.on_foot_target_button.setOnClickListener {
            onViewOnMapClickListener.invoke(adapterPosition)
        }
    }

    fun bind(onFootViewModel: OnFootViewModel) {
        val resources = itemView.context.resources

        itemView.on_foot_duration.text = resources.getString(
            R.string.duration_and_distance_braces,
            onFootViewModel.duration,
            onFootViewModel.distance
        )
    }
}