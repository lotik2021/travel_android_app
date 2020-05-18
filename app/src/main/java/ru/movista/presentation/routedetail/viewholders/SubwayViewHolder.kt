package ru.movista.presentation.routedetail.viewholders

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_route_detail_subway.view.*
import ru.movista.R
import ru.movista.presentation.viewmodel.SubwayViewModel

class SubwayViewHolder(
    view: View
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    fun bind(subwayViewModel: SubwayViewModel) {
        val lineInfoBg = ContextCompat.getDrawable(
            itemView.context,
            R.drawable.bg_subway_line_number
        ) as GradientDrawable

        val stopsImage = ContextCompat.getDrawable(
            itemView.context,
            R.drawable.trip
        ) as GradientDrawable


        val lineColor = Color.parseColor(subwayViewModel.bgColor)

        lineInfoBg.setColor(lineColor)
        stopsImage.setColor(lineColor)

        itemView.apply {
            subway_line_numbers.background = lineInfoBg
            subway_line_name.background = lineInfoBg
            subway_stops_image.background = stopsImage

            subway_line_numbers.text = subwayViewModel.lineNumber
            subway_line_name.text = subwayViewModel.lineName

            subway_departure_name.text = subwayViewModel.departureName
            subway_destination_name.text = subwayViewModel.destinationName

            subway_departure_time.text = subwayViewModel.startTime
            subway_destination_time.text = subwayViewModel.endTime

            subway_stops.text = context.getString(
                R.string.trip_duration_format,
                subwayViewModel.publicTransportStropsCount,
                subwayViewModel.tripDuration
            )
        }
    }
}