package ru.movista.presentation.routedetail.viewholders

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_route_detail_public.view.*
import ru.movista.R
import ru.movista.presentation.utils.inject
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.viewmodel.PublicTransportViewModel

class PublicTransportViewHolder(
    view: View,
    viewOnMapClickListener: (Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    init {
        itemView.public_transport_view_on_map_button.setOnClickListener {
            viewOnMapClickListener.invoke(adapterPosition)
        }
    }

    fun bind(publicTransportViewModel: PublicTransportViewModel) {
        itemView.apply {
            public_transport_type.setText(publicTransportViewModel.publicTransportName)
            public_transport_departure_name.text = publicTransportViewModel.publicTransportDepartureName
            public_transport_destination_name.text = publicTransportViewModel.publicTransportDestinationName
            public_transport_stops.text = context.getString(
                R.string.trip_duration_format,
                publicTransportViewModel.publicTransportStropsCount,
                publicTransportViewModel.tripDuration
            )

            public_transport_departure_time.text = publicTransportViewModel.tripStartTime
            public_transport_destination_time.text = publicTransportViewModel.tripEndTime
            public_transport_line_number.text = publicTransportViewModel.publicTransportLineNumber
        }

        val tripsStopsImage = ContextCompat.getDrawable(
            itemView.context,
            R.drawable.trip
        ) as GradientDrawable

        tripsStopsImage.setColor(
            ContextCompat.getColor(
                itemView.context,
                publicTransportViewModel.publicTransportColor
            )
        )

        itemView.trip_stops_image.background = tripsStopsImage // цвет применяется при установке background, но не при установке image

        itemView.trip_type_image.inject(
            publicTransportViewModel.publicTransportDetailIcon,
            publicTransportViewModel.publicTransportDetailIosIcon
        )

        if (publicTransportViewModel.publicTransportLineNumber.isNotEmpty()) {
            val lineInfoBg = ContextCompat.getDrawable(
                itemView.context,
                R.drawable.bg_subway_line_number
            ) as GradientDrawable

            lineInfoBg.setColor(
                ContextCompat.getColor(
                    itemView.context,
                    publicTransportViewModel.publicTransportColor
                )
            )

            itemView.public_transport_line_number.background = lineInfoBg
            itemView.public_transport_line_number.text = publicTransportViewModel.publicTransportLineNumber
            itemView.public_transport_line_number.setVisible()
        } else {
            itemView.public_transport_line_number.setGone()
        }

        if (publicTransportViewModel.publicTransportLineName.isNotEmpty()) {
            itemView.public_transport_line_name.text = publicTransportViewModel.publicTransportLineName
            itemView.public_transport_line_name.setVisible()
        } else {
            itemView.public_transport_line_name.setGone()
        }
    }
}