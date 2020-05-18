package ru.movista.presentation.tickets.segments

import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.item_carriage_type.view.*
import kotlinx.android.synthetic.main.item_ticket_segment.view.*
import ru.movista.R
import ru.movista.data.source.local.models.ObjectTripType
import ru.movista.data.source.local.models.TrainCoachType
import ru.movista.data.source.local.models.byPartner
import ru.movista.data.source.local.models.isTrain
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.Trip
import ru.movista.presentation.utils.*
import ru.movista.utils.*

class SegmentsAdapter(
    private val action: (Route) -> Unit
) : RecyclerView.Adapter<SegmentsAdapter.ItemHolder>() {
    companion object {
        private const val TIME_PATTERN = "H:mm"
        private const val DATE_PATTERN = "dd LLL"
    }

    private val items = mutableListOf<Route>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(parent.context.inflate(R.layout.item_ticket_segment, parent))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val route = items[position]

        if (route.tripsToServices.isEmpty()) {
            return
        }

        with(holder) {
            bind(route)
            bindPartners(route)
            bindTransfers(route)
            bindPriceGroup(route)
            bindDuration(route)
            bindProvidersBrands(route.tripsToServices.map { it.trip })
        }
    }

    fun updateItems(routes: List<Route>) {
        items.clear()
        items.addAll(routes)
        notifyDataSetChanged()
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(route: Route) {
            with(itemView) {
                itemView.segment_content.setScaleClick {
                    if (adapterPosition != NO_POSITION) {
                        action.invoke(route)
                    }
                }

                val title = route.tripsToServices
                    .mapNotNull { getRoutTitle(it.trip) }
                    .distinct()
                    .joinToString()

                segment_title.text = title
                from_place.text = route.fromDescription ?: ""
                to_place.text = route.toDescription ?: ""

                start_time.text = route.departureDate?.formatByPattern(TIME_PATTERN)
                start_date.text = route.departureDate?.formatByPattern(DATE_PATTERN)

                end_time.text = route.arrivalDate?.formatByPattern(TIME_PATTERN)
                end_date.text = route.arrivalDate?.formatByPattern(DATE_PATTERN)
            }
        }

        fun bindDuration(route: Route) {
            itemView.duration.text = minutesToDDHHMM(route.totalDuration)
        }

        fun bindPriceGroup(route: Route) {
            val alternatives = route.tripsToServices
                .filter { it.service.objectType.isTrain() }
                .mapNotNull { it.service.alternatives }
                .flatten()

            itemView.price_group_container.removeAllViews()

            if (alternatives.isEmpty()) {
                addPriceView(route.price)
            } else {
                alternatives
                    .sortedBy { it.coachType?.position }
                    .filter { it.coachType != TrainCoachType.OTHER }
                    .forEachIndexed { i, item ->
                        addPriceView(item.price, item.coachType, item.freeSeatsCount, i == 0)
                    }
            }
        }

        fun bindTransfers(route: Route) {
            with(itemView) {
                if (route.transferCount > 0) {
                    transfer_count.setVisible()
                    transfer_count.text = resources.getQuantityString(
                        R.plurals.changes_count,
                        route.transferCount,
                        route.transferCount
                    )
                } else {
                    transfer_count.setGone()
                }
            }
        }

        fun bindPartners(route: Route) {
            with(itemView) {
                val partnersTitle = route.tripsToServices
                    .filter { it.service.objectType.byPartner() }
                    .mapNotNull { it.service.bookingUrlCaption }
                    .filterNot { it.isBlank() }

                if (partnersTitle.isNotEmpty()) {
                    provider.setVisible()
                    provider.text = context
                        .getString(R.string.on_with, partnersTitle.first())
                        .toDefaultLowerCase()
                } else {
                    provider.setGone()
                }
            }
        }

        fun bindProvidersBrands(trip: List<Trip>) {
            with(itemView) {
                val providersBrandMargin = resources.getDimensionPixelSize(R.dimen.segments_providers_brand_margin)
                val providersBrandSize = resources.getDimensionPixelSize(R.dimen.segments_providers_brand_size)

                transports_types.removeAllViews()
                trip.forEachIndexed { index, trip ->
                    val providerImage = ImageView(context)
                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(providersBrandSize, providersBrandSize)
                    providerImage.setImageResource(R.drawable.ic_ellipse)

                    val colorResId: Int = when (trip.objectType) {
                        ObjectTripType.TRAIN -> R.color.primary_train
                        ObjectTripType.FLIGHT -> R.color.primary_flight
                        ObjectTripType.BUS, ObjectTripType.TRIP_BUS_TRANSFER -> R.color.primary_bus
                        ObjectTripType.TRAIN_SUBURBAN, ObjectTripType.TRIP_TRAIN_SUBURBAN_TRANSFER -> R.color.primary_suburban
                        ObjectTripType.TAXI -> R.color.primary_taxi
                        ObjectTripType.FOOT -> R.color.primary_foot
                        ObjectTripType.OTHER -> -1
                    }

                    if (colorResId != -1) {
                        if (index != 0) {
                            params.marginStart = providersBrandMargin
                        }

                        providerImage.setColorFilter(
                            ContextCompat.getColor(context, colorResId),
                            PorterDuff.Mode.SRC_IN
                        )
                        providerImage.layoutParams = params
                        transports_types.addView(providerImage)
                    }
                }

                transports_types.setVisibility(transports_types.childCount != 0)
            }
        }

        private fun addPriceView(
            price: Double?,
            coachType: TrainCoachType? = null,
            freeSeatsCount: Long? = null,
            isFirst: Boolean = true
        ) {
            with(itemView) {
                val item = context.inflate(R.layout.item_carriage_type)
                if (!isFirst) {
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.marginStart = context.resources.getDimensionPixelOffset(R.dimen.segments_carriage_margin)
                    item.layoutParams = lp
                }

                item.car_type.setVisible()
                item.free_seats.setVisible()

                if ((coachType == null || coachType == TrainCoachType.OTHER) && freeSeatsCount == null) {
                    item.price_description.setGone()
                } else {
                    item.price_description.setVisible()
                    item.free_seats.text = freeSeatsCount?.toString()

                    coachType?.let { coachType ->
                        if (coachType != TrainCoachType.OTHER) {
                            item.car_type.setText(coachType.titleResId)
                        }
                    }
                }

                item.price.setSmallCaps(
                    context.getString(
                        R.string.from_what,
                        price?.toPriceFormat()
                    )
                )

                price_group_container.addView(item)
            }
        }

        private fun getRoutTitle(trip: Trip?): String? {
            return when(trip?.objectType) {
                ObjectTripType.BUS -> trip.carrierName
                ObjectTripType.FLIGHT -> trip.opCarrierName
                ObjectTripType.TRAIN, ObjectTripType.TRAIN_SUBURBAN -> {
                    val title = trip.title
                    if (title.isNullOrBlank()) {
                        trip.carrierName
                    } else {
                        title
                    }
                }
                else -> null
            }
        }
    }
}