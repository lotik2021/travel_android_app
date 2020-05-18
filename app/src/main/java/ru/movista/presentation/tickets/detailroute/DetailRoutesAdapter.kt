package ru.movista.presentation.tickets.detailroute

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.item_bus_route_detail.view.*
import kotlinx.android.synthetic.main.item_flight_route_detail.view.*
import kotlinx.android.synthetic.main.item_flight_route_detail.view.item_prices_container
import kotlinx.android.synthetic.main.item_route_info.view.*
import kotlinx.android.synthetic.main.item_train_car_type.view.*
import kotlinx.android.synthetic.main.item_train_car_type.view.car_price
import kotlinx.android.synthetic.main.item_train_car_type.view.cars_types_title
import kotlinx.android.synthetic.main.item_train_car_type.view.free_place
import kotlinx.android.synthetic.main.item_train_route_detail.view.*
import kotlinx.android.synthetic.main.item_train_route_detail.view.price_description
import kotlinx.android.synthetic.main.item_train_route_detail.view.services
import kotlinx.android.synthetic.main.item_train_route_detail.view.services_description
import kotlinx.android.synthetic.main.item_train_route_detail.view.title
import kotlinx.android.synthetic.main.item_train_route_detail.view.title_image
import kotlinx.android.synthetic.main.item_train_route_detail.view.transport_description
import kotlinx.android.synthetic.main.item_train_route_detail.view.transport_title
import kotlinx.android.synthetic.main.item_transfer_route_detail.view.*
import ru.movista.R
import ru.movista.data.source.local.models.*
import ru.movista.domain.model.tickets.*
import ru.movista.presentation.custom.CheckableConstraintLayout
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setSmallCaps
import ru.movista.presentation.utils.setVisible
import ru.movista.utils.*

class DetailRoutesAdapter(private val actions: Actions) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TRAIN_VIEW_TYPE = 0
        private const val TRAIN_SUBURBAN_VIEW_TYPE = 1
        private const val BUS_VIEW_TYPE = 2
        private const val FLIGHT_VIEW_TYPE = 3
        private const val TRANSFER_VIEW_TYPE = 4

        private const val TIME_PATTERN = "H:mm"
        private const val DATE_PATTERN = "dd LLL"
    }

    private val items = mutableListOf<TripToService>()
    private var places: Map<Long, TripPlace> = emptyMap()
    private var route: Route? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return when (viewType) {
            TRAIN_VIEW_TYPE -> TrainHolder(
                context.inflate(
                    R.layout.item_train_route_detail,
                    parent
                )
            )
            BUS_VIEW_TYPE -> BusHolder(context.inflate(R.layout.item_bus_route_detail, parent))
            FLIGHT_VIEW_TYPE -> FlightHolder(
                context.inflate(
                    R.layout.item_flight_route_detail,
                    parent
                )
            )
            TRAIN_SUBURBAN_VIEW_TYPE -> SuburbanHolder(
                context.inflate(
                    R.layout.item_suburban_route_detail,
                    parent
                )
            )
            else -> TransferHolder(context.inflate(R.layout.item_transfer_route_detail, parent))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].trip.objectType) {
            ObjectTripType.FLIGHT -> FLIGHT_VIEW_TYPE
            ObjectTripType.TRAIN -> TRAIN_VIEW_TYPE
            ObjectTripType.TRAIN_SUBURBAN -> TRAIN_SUBURBAN_VIEW_TYPE
            ObjectTripType.BUS -> BUS_VIEW_TYPE
            else -> TRANSFER_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val trip = item.trip

        when (holder) {
            is TrainHolder -> {
                bindDirectionInfo(holder.itemView, trip)
                holder.bind(trip, item.service)
            }
            is SuburbanHolder -> {
                bindDirectionInfo(holder.itemView, trip)
                holder.bind(trip, item.service)
            }
            is BusHolder -> {
                bindDirectionInfo(holder.itemView, trip)
                holder.bind(trip, item.service)
            }
            is FlightHolder -> {
                bindDirectionInfo(holder.itemView, trip)
                holder.bind(trip, item.service)
            }
            is TransferHolder -> holder.bind(trip)
        }
    }

    fun updateItems(route: Route, tripPlaces: Map<Long, TripPlace>) {
        this.route = route
        this.places = tripPlaces
        items.clear()
        items.addAll(route.tripsToServices)
        notifyDataSetChanged()
    }

    private fun bindDirectionInfo(itemView: View, trip: Trip) {
        with(itemView) {
            departure_time.text = trip.departure?.formatByPattern(TIME_PATTERN)
            departure_day.text = trip.departure?.formatByPattern(DATE_PATTERN)
            arrival_time.text = trip.arrival?.formatByPattern(TIME_PATTERN)
            arrival_day.text = trip.arrival?.formatByPattern(DATE_PATTERN)
            trip.duration?.let { duration.text = minutesToDDHHMM(trip.duration) }

            val fromPlace = places[trip.fromId]
            val toPlace = places[trip.toId]
            departure_city.setSmallCaps(fromPlace?.cityName ?: "")
            from_point.text = fromPlace?.name
            arrival_city.setSmallCaps(toPlace?.cityName ?: "")
            arrival_point.text = toPlace?.name
        }
    }

    private fun getTripPrice(isFirstItem: Boolean, trip: Trip, service: RoutService): Double {
        var price = 0.0

        if (isFirstItem && PriceSegmentType.isTaken(route?.priceSegmentType)) {
            price = route?.price ?: 0.0
        } else if (isUniqueRouteService(trip, service)) {
            price = service.alternatives?.firstOrNull()?.price ?: 0.0
        }

        return price
    }

    private fun isUniqueRouteService(trip: Trip, service: RoutService): Boolean {
        val currentTripIndex = items.indexOfFirst { it.trip == trip }
        val currentServiceIndex = items.indexOfFirst { it.service.id == service.id }
        return currentTripIndex == currentServiceIndex
    }

    private fun getTripPrefixPrice(price: Double, trip: Trip, service: RoutService): String {
        val isEmptyPrefix = (price == 0.0 || !(PriceSegmentType.isTaken(route?.priceSegmentType) || !isUniqueRouteService(trip, service)))
        return if (isEmptyPrefix) "" else "+ "
    }

    private inner class TrainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            with(itemView) {
                itinerary.paint.isUnderlineText = true

                transport_icon_indicator.setImageResource(R.drawable.movista_train)
                route_departure_indicator.setTint(R.color.primary_train)
                route_arrival_indicator.setTint(R.color.primary_train)
                route_direction_line.setBackgroundColor(getColor(context, R.color.primary_train))
            }
        }

        fun bind(trip: Trip, service: RoutService?) {
            with(itemView) {
                transport_title.setSmallCaps(R.string.train_small)
                title.setSmallCaps(trip.carrierName ?: "")

                if (trip.number.isNullOrBlank()) {
                    transport_description.text = trip.carTypeName
                } else {
                    val firstText =  "${trip.number}, "
                    val secondText = trip.carTypeName ?: ""
                    val resultText = "$firstText$secondText"
                    val standardTextSize = 14

                    transport_description.setSpan(resultText, 0, firstText.length, standardTextSize, R.font.roboto_medium)
                }

                services.setSmallCaps(R.string.services)
                if (trip.hasElectronicRegistration == true) {
                    services_description.setText(R.string.hasElectronicRegistration)
                    services_description.setTextColor(getColor(context, R.color.blue_primary))
                } else {
                    services_description.setText(R.string.hasNotElectronicRegistration)
                    services_description.setTextColor(getColor(context, R.color.primary_red))
                }

                val alternatives = service?.alternatives
                price_description.setSmallCaps(R.string.train_cars_types)

                if (alternatives.isNullOrEmpty()) {
                    prices_container.setGone()
                    price_description.setGone()
                    return
                } else {
                    prices_container.setVisible()
                    price_description.setVisible()
                    prices_container.removeAllViews()
                }

                val sortedAlternatives = alternatives.sortedBy { it.coachType?.position }

                var selectedAlternative: Alternative? = null
                service.selectedAlternative?.let { selectedAlternative = it }

                if (selectedAlternative == null) {
                    selectedAlternative = sortedAlternatives.firstOrNull()
                }

                for (i in sortedAlternatives.indices) {
                    val alternative = sortedAlternatives[i]
                    val coachType = alternative.coachType

                    if (coachType == null || coachType == TrainCoachType.OTHER) {
                        continue
                    }

                    val view = context.inflate(R.layout.item_train_car_type)
                    prices_container.addView(view)
                    view.сheckable_item_prices_container.isRadioLayout = true

                    if (selectedAlternative == alternative) {
                        view.сheckable_item_prices_container.isChecked = true
                        actions.onSelectedAlternative(sortedAlternatives[i])
                    }

                    view.cars_types_title.setText(coachType.titleResId)
                    view.free_place.text = getString(R.string.seats_count_title, alternative.freeSeatsCount ?: 0L)
                    view.car_price.setSmallCaps(alternative.price?.toPriceFormat() ?: "")

                    view.сheckable_item_prices_container.setOnCheckedChangeListener { checkableView, isChecked ->
                        if (!isChecked) {
                            return@setOnCheckedChangeListener
                        }

                        for (index in 0 until prices_container.childCount) {
                            val childView = prices_container.getChildAt(index)
                            if (childView is CheckableConstraintLayout) {
                                if (childView == checkableView) {
                                    actions.onSelectedAlternative(sortedAlternatives[i])
                                } else {
                                    childView.isChecked = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private inner class SuburbanHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            with(itemView) {
                itinerary.paint.isUnderlineText = true
                transport_icon_indicator.setImageResource(R.drawable.movista_suburban)
                route_departure_indicator.setTint(R.color.primary_suburban)
                route_arrival_indicator.setTint(R.color.primary_suburban)
                route_direction_line.setBackgroundColor(getColor(context, R.color.primary_suburban))
            }
        }

        fun bind(trip: Trip, service: RoutService?) {
            with(itemView) {
                transport_title.setSmallCaps(R.string.suburban_small)
                title.setSmallCaps(trip.carrierName ?: "")
//                transport_number.text = trip.number
                transport_description.text = trip.carTypeName

                services.setSmallCaps(R.string.services)
                if (trip.hasElectronicRegistration == true) {
                    services_description.setText(R.string.hasElectronicRegistration)
                    services_description.setTextColor(getColor(context, R.color.blue_primary))
                } else {
                    services_description.setText(R.string.hasNotElectronicRegistration)
                    services_description.setTextColor(getColor(context, R.color.primary_red))
                }

                val alternatives = service?.alternatives
                price_description.setSmallCaps(R.string.price_title)

                if (alternatives.isNullOrEmpty()) {
                    item_prices_container.setGone()
                    price_description.setGone()
                    return
                } else {
                    item_prices_container.setVisible()
                    price_description.setVisible()
                }

                val alternative = alternatives.first()

                free_place.text = getString(R.string.seats_count_title, alternative.freeSeatsCount ?: 0L)
                car_price.setSmallCaps(alternative.price?.toPriceFormat() ?: "")
            }
        }
    }

    private inner class BusHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            with(itemView) {
                transport_icon_indicator.setImageResource(R.drawable.movista_bus)
                route_departure_indicator.setTint(R.color.primary_bus)
                route_arrival_indicator.setTint(R.color.primary_bus)
                route_direction_line.setBackgroundColor(getColor(context, R.color.primary_bus))
            }
        }

        fun bind(trip: Trip, service: RoutService?) {
            with(itemView) {
                transport_title.setSmallCaps(R.string.bus_small)
                title.setSmallCaps(trip.carrierName ?: "")
                primary_description.text = trip.direction
                secondary_description.text = trip.description

                info_title.setSmallCaps(R.string.info_title)

                val alternatives = service?.alternatives
                price_description.setSmallCaps(R.string.price_title)

                if (alternatives.isNullOrEmpty()) {
                    item_prices_container.setGone()
                    price_description.setGone()
                    return
                } else {
                    item_prices_container.setVisible()
                    price_description.setVisible()
                }

                val alternative = alternatives.first()
                free_place.text = getString(R.string.seats_count_title, alternative.freeSeatsCount ?: 0L)

                val tripPrice = getTripPrice(adapterPosition == 0, trip, service)
                val prefix = getTripPrefixPrice(tripPrice, trip, service)
                car_price.setSmallCaps(prefix + tripPrice.toPriceFormat())

                if (tripPrice == 0.0) {
                    bus_price_info.setVisible()
                } else {
                    bus_price_info.setGone()
                }

                bus_price_info.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        actions.onPriceInfoClick()
                    }
                }
            }
        }
    }

    private inner class FlightHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            with(itemView) {
                transport_icon_indicator.setImageResource(R.drawable.movista_flight)
                route_departure_indicator.setTint(R.color.primary_flight)
                route_arrival_indicator.setTint(R.color.primary_flight)
                route_direction_line.setBackgroundColor(getColor(context, R.color.primary_flight))
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(trip: Trip, service: RoutService?) {
            with(itemView) {
                transport_title.setSmallCaps(R.string.plane_small)

                if (!trip.opCarrierName.isNullOrBlank() && trip.markCarrierName != trip.opCarrierName) {
                    val firstText = trip.markCarrierName ?: ""
                    val secondText = getString(R.string.transport_performs, trip.opCarrierName)
                    val resultText = "$firstText $secondText"
                    val secondTextSize = 14

                    title.setSpan(resultText, firstText.length, resultText.length, secondTextSize, R.font.roboto_regular)
                } else {
                    title.text = trip.markCarrierName
                }

                if (trip.number.isNullOrBlank()) {
                    transport_description.text = trip.carTypeName
                } else {
                    val firstText =  "${trip.number}, "
                    val secondText = trip.carTypeName ?: ""
                    val resultText = "$firstText$secondText"
                    val standardTextSize = 14

                    transport_description.setSpan(resultText, 0, firstText.length, standardTextSize, R.font.roboto_medium)
                }

                tariff_title.setSmallCaps(R.string.about_tariff)

                val segmentOption = service?.serviceSegmentOption
                val optionsTypes = segmentOption?.optionsTypes

                setOptionDescription(carry_on_baggage, optionsTypes?.baggage, R.string.baggage_with)
                setOptionDescription(baggage, optionsTypes?.cabin, R.string.carry_on_baggage_with)
                setOptionDescription(exchange_ticket, optionsTypes?.exchangeable, R.string.exchange_with)
                setOptionDescription(ticket_refund, optionsTypes?.refundable, R.string.refund_with)

                val alternatives = service?.alternatives
                price_description.setSmallCaps(R.string.price_title)

                if (alternatives.isNullOrEmpty()) {
                    item_prices_container.setGone()
                    price_description.setGone()
                    return
                } else {
                    item_prices_container.setVisible()
                    price_description.setVisible()
                }

                if (service.objectType.isFlight()) {
                    if (segmentOption?.fareFamilyName.isNullOrBlank()) {
                        if (segmentOption?.comfortType == null) {
                            cars_types_title.setText(R.string.ticket)
                        } else {
                            cars_types_title.setText(segmentOption.comfortType.resId)
                        }
                    } else {
                        cars_types_title.text = segmentOption?.fareFamilyName
                    }
                } else {
                    cars_types_title.setText(R.string.ticket)
                }

                free_place.text = getString(R.string.seats_count_title, segmentOption?.freeSeatsCount ?: 0L)

                val tripPrice = getTripPrice(adapterPosition == 0, trip, service)
                val prefix = getTripPrefixPrice(tripPrice, trip, service)
                car_price.setSmallCaps(prefix + tripPrice.toPriceFormat())

                if (tripPrice == 0.0) {
                    flight_price_info.setVisible()
                } else {
                    flight_price_info.setGone()
                }

                flight_price_info.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        actions.onPriceInfoClick()
                    }
                }
            }
        }

        private fun setOptionDescription(
            optionView: TextView,
            serviceOptionInfo: ServiceOptionInfo?,
            optionTitleRes: Int
        ) {
            with(itemView) {
                optionView.setTextColor(getColor(context, R.color.blue_primary))
                val serviceOptionColorRes: Int

                val serviceOptionText = if (serviceOptionInfo == null || serviceOptionInfo.serviceOptionInfoType == ServiceOptionInfoType.UNKNOWN) {
                    serviceOptionColorRes = R.color.primary_red
                    getString(R.string.unknown)
                } else if (serviceOptionInfo.serviceOptionInfoType == ServiceOptionInfoType.FREE) {
                    if (serviceOptionInfo.description.isNullOrBlank()) {
                        serviceOptionColorRes = R.color.primary_red
                        getString(R.string.unknown)
                    } else {
                        serviceOptionColorRes = R.color.blue_primary
                        serviceOptionInfo.description
                    }
                } else if (serviceOptionInfo.serviceOptionInfoType == ServiceOptionInfoType.PAY) {
                    serviceOptionColorRes = R.color.blue_primary
                    getString(R.string.with_collection)
                } else if (serviceOptionInfo.serviceOptionInfoType == ServiceOptionInfoType.NOT_AVAILABLE) {
                    serviceOptionColorRes = R.color.primary_red
                    getString(R.string.not_available)
                } else {
                    serviceOptionColorRes = R.color.primary_red
                    getString(R.string.unknown)
                }

                optionView.setTextColor(getColor(context, serviceOptionColorRes))
                optionView.text = getString(optionTitleRes, serviceOptionText.toDefaultLowerCase())
            }
        }
    }

    private inner class TransferHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(trip: Trip) {
            with(itemView) {
                val duration = if (trip.duration == null) "" else minutesToDDHHMM(trip.duration)

                val fromPlace = places[trip.fromId]
                val toPlace = places[trip.toId]

                transfer_title.setSmallCaps(getString(R.string.transfer_duration, duration))
                city.text = fromPlace?.cityName

                if (fromPlace == toPlace) {
                    description.text = getString(R.string.transfer_not_required, fromPlace?.name)
                } else {
                    description.text = getString(R.string.dash_split_text, fromPlace?.name, toPlace?.name)
                }

                if (trip.objectType == ObjectTripType.TAXI) {
                    title_image.setVisible()
                } else {
                    title_image.setGone()
                }
            }
        }
    }

    interface Actions {
        fun onSelectedAlternative(alternative: Alternative)
        fun onPriceInfoClick()
    }
}
