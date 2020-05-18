package ru.movista.presentation.routedetail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_route_detail.view.*
import ru.movista.presentation.utils.TripsPopulator
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.viewmodel.GoogleRouteDetailsViewModel
import ru.movista.presentation.viewmodel.TripViewModel

class RouteDetailLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    lateinit var adapter: RouteDetailAdapter

    private var overallScrollY = 0

    fun setupRouteDetailsRecycler(
        taxiClickListener: (Int, Int) -> Unit,
        agenciesInfoClickListener: () -> Unit,
        viewOnMapClickListener: (Int) -> Unit
    ) {
        LayoutInflater.from(context).inflate(ru.movista.R.layout.layout_route_detail, this, true)

        route_detail_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        adapter = RouteDetailAdapter(taxiClickListener, agenciesInfoClickListener, viewOnMapClickListener)

        route_detail_recycler_view.adapter = adapter

        route_detail_recycler_view.setHasFixedSize(true)
    }

    @Suppress("UNCHECKED_CAST")
    fun setRouteInfo(
        routeViewModel: GoogleRouteDetailsViewModel,
        baseTrips: List<TripViewModel>,
        showFooterInfo: Boolean
    ) {

        TripsPopulator.populateTrips(trips_container, baseTrips)
        route_duration.text = routeViewModel.durationMinutes
        route_start_to_end_time.text = routeViewModel.startToEndTime

        setTripsInfo(routeViewModel, showFooterInfo)
    }

    fun dialogFullyOpenStateChanged(fullyOpen: Boolean) {
        if (fullyOpen) {
            route_detail_recycler_view.addOnScrollListener(scrollListener)
        } else {
            // если детали маршрута скрыли - вернуть скролл в начальное состояние и убрать тень.
            route_detail_recycler_view.scrollToPosition(0)
            route_detail_recycler_view.clearOnScrollListeners()
            overallScrollY = 0
            shadow.setGone()
        }
    }

    fun clearSelf() {
        route_detail_recycler_view.adapter = null
    }

    private fun setTripsInfo(routeViewModel: GoogleRouteDetailsViewModel, showFooterInfo: Boolean) {

        adapter.setItems(routeViewModel.trips, showFooterInfo)
    }

    private val scrollListener = object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            overallScrollY += dy

            // Показываем тень над списком, если список не в начальном состоянии
            if (overallScrollY != 0) shadow.setVisible() else shadow.setGone()
        }

    }
}
