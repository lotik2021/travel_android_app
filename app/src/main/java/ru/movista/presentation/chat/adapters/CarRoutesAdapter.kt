package ru.movista.presentation.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_car.view.*
import ru.movista.R
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.viewmodel.CarRouteViewModel

class CarRoutesAdapter(
    private val clickListener: (Int) -> Unit,
    private val routes: List<CarRouteViewModel>
) : androidx.recyclerview.widget.RecyclerView.Adapter<CarRoutesAdapter.ViewHolder>() {

    init {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false))
    }

    override fun getItemCount() = routes.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(routes[position])
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                clickListener.invoke(adapterPosition)
            }
        }

        fun bind(routeViewModel: CarRouteViewModel) {
            with(itemView) {
                car_duration.text = resources.getString(
                    R.string.approximately_duration,
                    routeViewModel.carMaxDuration
                )

                if (routeViewModel.carRouteThrough.isNotEmpty()) {
                    car_route_through.setVisible()
                    car_additional_info.setVisible()
                    car_additional_info.text = routeViewModel.carRouteThrough
                } else {
                    car_route_through.setGone()
                    car_additional_info.setGone()
                }
            }
        }
    }
}