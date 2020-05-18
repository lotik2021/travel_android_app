package ru.movista.presentation.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import org.jetbrains.anko.image
import ru.movista.R
import ru.movista.presentation.viewmodel.*
import ru.movista.utils.getDimension

object TripsPopulator {
    private const val MIN_DURATION_TO_VISIBLE = 5

    fun populateTrips(layout: LinearLayout, trips: List<TripViewModel>) {
        val layoutParams = LinearLayout.LayoutParams(
            layout.getDimension(R.dimen.size_large),
            layout.getDimension(R.dimen.size_large)
        )

        layout.removeAllViews()

        trips.forEachIndexed { index, tripViewModel ->
            val imageView = ImageView(layout.context)

            when (tripViewModel) {
                is OnFootViewModel -> {
                    imageView.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    imageView.setImageResource(R.drawable.trip_on_foot)
                }
                is TaxiViewModel -> imageView.setImageResource(R.drawable.trip_taxi)
                else -> {
                    imageView.layoutParams = layoutParams
                    imageView.inject(tripViewModel.tripIcon, tripViewModel.iosTripIcon)
                }
            }

            if (tripViewModel is SubwayViewModel) {
                setBgColorForSubwayIcon(layout, tripViewModel, imageView)
            }

            layout.addView(imageView)

            val duration = tripViewModel.tripDuration.filter { it.isDigit() }.toIntOrNull()
            if (duration != null && tripViewModel is OnFootViewModel && duration >= MIN_DURATION_TO_VISIBLE) {
                val durationLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM
                }

                val durationTextView = TextView(layout.context)
                durationTextView.layoutParams = durationLayoutParams
                durationTextView.text = tripViewModel.tripDuration
                durationTextView.typeface = ResourcesCompat.getFont(layout.context, R.font.roboto_medium)
                durationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
                layout.addView(durationTextView)
            }

            if (index != trips.lastIndex) {
                val nextRouteImage = ImageView(layout.context)
                nextRouteImage.image = ContextCompat.getDrawable(layout.context, R.drawable.trip_next_arrow)
                nextRouteImage.layoutParams = layoutParams
                layout.addView(nextRouteImage)
            }
        }
    }

    fun populateLongTrips(layout: LinearLayout, trips: List<LongRouteTripType>) {
        val context = layout.context

        val layoutParams =
            LinearLayout.LayoutParams(
                layout.context.resources.getDimensionPixelSize(R.dimen.size_medium_plus),
                layout.context.resources.getDimensionPixelSize(R.dimen.size_medium_plus)
            )
        layoutParams.leftMargin = context.resources.getDimensionPixelSize(R.dimen.size_small)

        layout.removeAllViews()

        trips.forEach { tripViewModel ->

            val imageView = ImageView(context)
            imageView.image = ContextCompat.getDrawable(context, tripViewModel.icon)
            imageView.layoutParams = layoutParams
            layout.addView(imageView)
        }
    }

    private fun setBgColorForSubwayIcon(
        layout: LinearLayout,
        type: SubwayViewModel,
        imageView: ImageView
    ) {
        val backgroundShape = ContextCompat.getDrawable(
            layout.context,
            R.drawable.bg_trip_subway
        ) as GradientDrawable

        backgroundShape.setColor(Color.parseColor(type.bgColor))
        imageView.background = backgroundShape
    }
}