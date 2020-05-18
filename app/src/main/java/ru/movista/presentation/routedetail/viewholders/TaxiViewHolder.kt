package ru.movista.presentation.routedetail.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_route_detail_taxi.view.*
import kotlinx.android.synthetic.main.item_taxi_provider.view.*
import ru.movista.R
import ru.movista.presentation.utils.inject
import ru.movista.presentation.viewmodel.TaxiViewModel
import ru.movista.utils.inflate

class TaxiViewHolder(
    view: View,
    private val clickListener: (Int, Int) -> Unit,
    onViewOnMapClickListener: (Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    init {
        itemView.trip_taxi_target_button.setOnClickListener {
            onViewOnMapClickListener.invoke(adapterPosition)
        }
    }

    fun bind(taxiViewModel: TaxiViewModel) {
        with(itemView) {
            trip_taxi_departure_name.text = taxiViewModel.fromAddress
            trip_taxi_destination_name.text = taxiViewModel.toAddress
            trip_taxi_departure_time.text = taxiViewModel.startTime
            trip_taxi_destination_time.text = taxiViewModel.endTime

            val providersContainer = trip_taxi_providers
            providersContainer.removeAllViews()

            taxiViewModel.providers.forEachIndexed { index, item ->
                val taxiProvider = context.inflate(R.layout.item_taxi_provider, providersContainer)

                taxiProvider.setOnClickListener {
                    clickListener.invoke(adapterPosition, index)
                }

                taxiProvider.taxi_provider_image.inject(item.taxiProviderImage, item.taxiProviderIosImage)
                taxiProvider.taxi_provider_name.text = item.taxiProviderDescription
                taxiProvider.taxi_min_price.text =
                    context.getString(R.string.price_from, item.taxiProviderMinPrice)

                providersContainer.addView(taxiProvider)
            }
        }
    }
}
