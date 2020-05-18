package ru.movista.presentation.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_movista_route.view.*
import ru.movista.R
import ru.movista.presentation.utils.TripsPopulator
import ru.movista.presentation.viewmodel.MovistaRouteViewModel

class LongRoutesAdapter(private val clickListener: (Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.Adapter<LongRoutesAdapter.ViewHolder>() {

    private val routes = arrayListOf<MovistaRouteViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_movista_route, parent, false))
    }

    override fun getItemCount() = routes.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(routes[position])
    }

    fun setItems(routeList: List<MovistaRouteViewModel>) {
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

        fun bind(routeViewModel: MovistaRouteViewModel) {
            with(itemView) {
                movista_route_departure_date.text = routeViewModel.startDate
                movista_route_destination_date.text = routeViewModel.endDate
                movista_route_departure_time.text = routeViewModel.startTime
                movista_route_destination_time.text = routeViewModel.endTime
                movista_route_duration.text = routeViewModel.duration
                movista_route_changes_count.text = routeViewModel.changesCount
                movista_route_price.text = routeViewModel.price
                TripsPopulator.populateLongTrips(movista_trips_container, routeViewModel.trips)
            }
        }
    }
}