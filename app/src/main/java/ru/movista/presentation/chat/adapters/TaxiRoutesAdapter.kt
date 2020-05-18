package ru.movista.presentation.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_taxi_route.view.*
import ru.movista.R
import ru.movista.presentation.utils.inject
import ru.movista.presentation.viewmodel.TaxiProviderViewModel
import ru.movista.utils.EMPTY

class TaxiRoutesAdapter(private val clickListener: (Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.Adapter<TaxiRoutesAdapter.ViewHolder>() {

    private val routes = arrayListOf<TaxiProviderViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_taxi_route, parent, false))
    }

    override fun getItemCount() = routes.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(routes[position])
    }

    fun setItems(routeList: List<TaxiProviderViewModel>) {
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

        fun bind(taxiProviderViewModel: TaxiProviderViewModel) {
            val resources = itemView.context.resources
            with(taxiProviderViewModel) {
                itemView.taxi_image.inject(this.taxiProviderImage, this.taxiProviderIosImage)
                itemView.taxi_name.text = taxiProviderDescription
                itemView.taxi_duration.text = taxiProviderDuration

                itemView.taxi_tariff_name.text = taxiProviderTariffs[0].taxiTariffName
                itemView.taxi_min_price.text = itemView.context
                    .getString(R.string.price_from, taxiProviderMinPrice)
                    .capitalize()

                if (taxiProviderStartTime != String.EMPTY && taxiProviderEndTime != String.EMPTY) {
                    itemView.taxi_start_to_end_time.text = resources.getString(
                        R.string.start_time_to_end_time,
                        taxiProviderStartTime,
                        taxiProviderEndTime
                    )
                }
            }
        }
    }
}