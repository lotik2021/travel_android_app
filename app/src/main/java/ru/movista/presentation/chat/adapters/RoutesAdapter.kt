package ru.movista.presentation.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_route.view.*
import ru.movista.R
import ru.movista.presentation.utils.TripsPopulator
import ru.movista.presentation.viewmodel.RouteViewModel

class RoutesAdapter(private val clickListener: (Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    private val routes = arrayListOf<RouteViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false))
    }

    override fun getItemCount() = routes.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(routes[position])
    }

    fun setItems(routeList: List<RouteViewModel>) {
        routes.clear()
        routes.addAll(routeList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                clickListener.invoke(adapterPosition)
            }
        }

        fun bind(routeViewModel: RouteViewModel) {
            itemView.route_start_to_end_time.text = routeViewModel.startToEndTime
            itemView.taxi_duration.text = routeViewModel.durationWithHours
            TripsPopulator.populateTrips(itemView.trips_container, routeViewModel.trips)
        }
    }
}