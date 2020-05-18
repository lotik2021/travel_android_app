package ru.movista.presentation.routedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.movista.R
import ru.movista.presentation.common.EmptyViewHolder
import ru.movista.presentation.routedetail.viewholders.*
import ru.movista.presentation.viewmodel.*

class RouteDetailAdapter(
    private val onTaxiProviderClickedListener: (Int, Int) -> Unit,
    private val onAgenciesInfoClickListener: () -> Unit,
    private val onViewOnMapClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ON_FOOT = 0

        private const val PUBLIC_TRANSPORT = 1

        private const val SUBWAY = 2
        private const val TAXI = 3
        private const val DESTINATION = 5

        private const val FOOTER_INFO = 6

        private const val TRANSIT = 7
    }

    private var items: ArrayList<TripDetailsViewModel> = arrayListOf()

    private var showFooterInfo: Boolean = false

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return FOOTER_INFO
        }

        return when (items[position]) {
            is OnFootViewModel -> ON_FOOT
            is SubwayViewModel -> SUBWAY
            is DestinationDetailViewModel -> DESTINATION
            is TaxiViewModel -> TAXI
            is TransitViewModel -> TRANSIT
            else -> PUBLIC_TRANSPORT
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ON_FOOT -> {
                OnFootViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_route_detail_on_foot, parent, false),
                    onViewOnMapClickListener
                )
            }
            PUBLIC_TRANSPORT -> {
                PublicTransportViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_route_detail_public, parent, false),
                    onViewOnMapClickListener
                )
            }
            SUBWAY -> {
                SubwayViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_route_detail_subway, parent, false)
                )
            }
            DESTINATION -> {
                DestinationViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_route_detail_destination, parent, false)
                )
            }
            TAXI -> {
                TaxiViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_route_detail_taxi, parent, false),
                    onTaxiProviderClickedListener,
                    onViewOnMapClickListener
                )
            }
            FOOTER_INFO -> {
                if (showFooterInfo) {
                    FooterInfoViewHolder(
                        LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.item_route_detail_info, parent, false),
                        onAgenciesInfoClickListener
                    )
                } else {
                    EmptyViewHolder(View(parent.context))
                }
            }

            TRANSIT -> {
                TransitViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_route_detail_transit, parent, false)
                )
            }

            else -> throw IllegalStateException("Undefined ViewType")
        }
    }

    override fun getItemCount() = items.size + 1 // plus footer info

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (position > items.lastIndex) {
            // footer пропускаем
            return
        }
        val currentItem = items[position]
        when (viewHolder) {
            is OnFootViewHolder -> viewHolder.bind(currentItem as OnFootViewModel)
            is PublicTransportViewHolder -> viewHolder.bind(currentItem as PublicTransportViewModel)
            is SubwayViewHolder -> viewHolder.bind(currentItem as SubwayViewModel)
            is TaxiViewHolder -> viewHolder.bind(currentItem as TaxiViewModel)
            is TransitViewHolder -> viewHolder.bind(currentItem as TransitViewModel)
            is DestinationViewHolder -> {
                // необходимо проверить, не пустой ли список элементов, чтобы обратиться к предпоследнему
                val previousIsTaxi =
                    if (items.size > 1) items[items.lastIndex - 1] is TaxiViewModel else false

                viewHolder.bind(
                    currentItem as DestinationDetailViewModel,
                    previousIsTaxi
                )
            }
        }
    }

    fun setItems(tripDetails: List<TripDetailsViewModel>, showFooterInfo: Boolean) {
        this.showFooterInfo = showFooterInfo
        items.clear()
        items.addAll(tripDetails)
        notifyDataSetChanged()
    }

}
