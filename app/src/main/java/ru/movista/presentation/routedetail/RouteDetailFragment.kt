package ru.movista.presentation.routedetail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.fragment_route_detail.*
import kotlinx.android.synthetic.main.fragment_route_detail.route_detail_car_navigation
import kotlinx.android.synthetic.main.fragment_route_detail.route_detail_taxi_order
import kotlinx.android.synthetic.main.layout_route_detail.*
import kotlinx.android.synthetic.main.layout_route_detail.view.*
import kotlinx.android.synthetic.main.view_map.*
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.topPadding
import ru.movista.R
import ru.movista.data.repository.TaxiOrderRepository
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.carnavigation.CarNavigationViewDelegate
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.common.mapview.MapFragment
import ru.movista.presentation.refilltravelcard.RefillTravelCardDialogFragment
import ru.movista.presentation.refilltravelcard.TravelCardViewModel
import ru.movista.presentation.taxiorder.TaxiOrderDialogFragment
import ru.movista.presentation.taxiorder.TaxiOrderViewDelegate
import ru.movista.presentation.utils.*
import ru.movista.presentation.viewmodel.*

class RouteDetailFragment : BaseFragment(), RouteDetailView, OnBackPressedListener {

    companion object {

        private const val KEY_EXTRA = "extra"

        private const val KEY_TAXI_ORDER_BOTTOM_DIALOG = "taxi_order"

        private const val TAG_REFILL_TRAVEL_CARD_BOTTOM_DIALOG = "refill_travel_card_dialog"

        private const val KEY_DRAG_IV_PADDING = "drag_iv_padding"

        fun newInstance(routeViewModel: RouteDetailsViewModel): RouteDetailFragment {
            val instance = RouteDetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_EXTRA, routeViewModel)
            instance.arguments = bundle
            return instance
        }
    }

    private var mapFragment: MapFragment? = null

    private var behavior: BottomSheetBehavior<RouteDetailLayout>? = null

    private var carNavigationViewDelegate: CarNavigationViewDelegate? = null
    private var taxiOrderViewDelegate: TaxiOrderViewDelegate? = null

    private var dragIVPadding = 0

    @InjectPresenter
    lateinit var presenter: RouteDetailPresenter

    @ProvidePresenter
    fun providePresenter(): RouteDetailPresenter = RouteDetailPresenter(arguments?.getSerializable(KEY_EXTRA))

    override fun sharePresenter(): BasePresenter<MvpView>? = presenter as? BasePresenter<MvpView>

    override fun getLayoutRes() = R.layout.fragment_route_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        dragIVPadding = savedInstanceState?.getInt(KEY_DRAG_IV_PADDING) ?: 0
    }

    override fun initUI() {
        super.initUI()

        back_button.setOnClickListener { activity?.onBackPressed() }
    }

    override fun afterCreate() {
        super.afterCreate()

        mapFragment = childFragmentManager.findFragmentById(R.id.map_fr) as MapFragment
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_DRAG_IV_PADDING, dragIVPadding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (bottom_dialog.isVisible()) bottom_dialog.clearSelf()
        behavior?.setBottomSheetCallback(null)
        behavior = null
    }

    override fun showRouteOnMap(
        polyline: String,
        color: Int,
        solidLine: Boolean,
        showAllRoute: Boolean
    ) {
        val pattern = if (!solidLine) listOf(Dash(48f), Gap(12f)) else null

        mapFragment?.googleMapHelper?.drawPolyline(
            GoogleMapHelper.PolylineOptions(polyline, color, pattern)
        )

        val decodedPolyline = PolyUtil.decode(polyline)

        if (decodedPolyline.isNotEmpty()) {
            showStartIconOnMap(decodedPolyline.first())
            showEndIconOnMap(decodedPolyline.last())
        }

        if (showAllRoute) {
            showAllRoute(polyline, polyline)
        }
    }

    override fun showTripsOnMap(
        trips: List<TripViewModel>,
        solidLine: Boolean,
        showAllRoute: Boolean
    ) {

        if (trips.isEmpty()) return

        drawEachPolyline(trips, solidLine)

        val firstTripPolyline = PolyUtil.decode(trips.first().tripPolyline)

        if (firstTripPolyline.isNotEmpty()) {
            showStartIconOnMap(firstTripPolyline.first())
        }

        val lastTripPolyline = PolyUtil.decode(trips.last().tripPolyline)

        if (lastTripPolyline.isNotEmpty()) {
            showEndIconOnMap(lastTripPolyline.last())
        }

        if (showAllRoute) {
            showAllRoute(trips.first().tripPolyline, trips.last().tripPolyline)
        }
    }

    override fun clearDrawnRoute() {
        mapFragment?.googleMapHelper?.clearRoute()
    }

    override fun showGoogleRouteDetail(
        routeViewModel: GoogleRouteDetailsViewModel,
        baseTrips: List<TripViewModel>,
        showFooterInfo: Boolean
    ) {
        setupBottomDialog()

        bottom_dialog.setRouteInfo(routeViewModel, baseTrips, showFooterInfo)
    }

    override fun showCarRouteDetails(carRouteViewModel: CarRouteViewModel) {
        route_detail_car_navigation.setVisible()

        changeCurrentLocationIconBottomConstraint(toTopOf = R.id.route_detail_car_navigation)

        carNavigationViewDelegate = CarNavigationViewDelegate(
            route_detail_car_navigation,
            carRouteViewModel,
            childFragmentManager
        ).apply { initUI() }
    }

    override fun showTaxiOrderDetails(
        taxiProviderViewModel: TaxiProviderViewModel,
        taxiOrderRepository: TaxiOrderRepository
    ) {
        route_detail_taxi_order.setVisible()

        changeCurrentLocationIconBottomConstraint(toTopOf = R.id.route_detail_taxi_order)

        taxiOrderViewDelegate = TaxiOrderViewDelegate(
            route_detail_taxi_order,
            taxiOrderRepository,
            taxiProviderViewModel,
            analyticsManager
        ).apply { initUI() }
    }


    override fun onBackPressed() {
        presenter.onBackPressed(behavior?.state == BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun collapseBottomDialog() {
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun showTaxiOrderDialog(taxiProviderInfo: TaxiProviderViewModel) {
        val taxiOrderDialogFragment = TaxiOrderDialogFragment.getInstance(taxiProviderInfo)
        taxiOrderDialogFragment.show(childFragmentManager, KEY_TAXI_ORDER_BOTTOM_DIALOG)
    }

    override fun showRefillTravelCardFabLoading() {
        bottom_dialog.route_detail_refill_travel_card_fab.isEnabled = false
        bottom_dialog.route_detail_refill_travel_card_fab.setInVisible()
        bottom_dialog.route_detail_travel_card_loading.setVisible()
    }

    override fun hideRefillTravelCardFabLoading() {
        bottom_dialog.route_detail_refill_travel_card_fab.isEnabled = true
        bottom_dialog.route_detail_refill_travel_card_fab.setVisible()
        bottom_dialog.route_detail_travel_card_loading.setGone()
    }

    override fun showRefillTravelCardDialog(travelCardViewModel: ArrayList<TravelCardViewModel>) {
        val refillTravelCardDialogFragment =
            RefillTravelCardDialogFragment.newInstance(travelCardViewModel)

        refillTravelCardDialogFragment.show(childFragmentManager, TAG_REFILL_TRAVEL_CARD_BOTTOM_DIALOG)
    }


    override fun zoomInTrip(tripPolyline: String) {
        mapFragment?.googleMapHelper?.fillFullPolylineIntoMap(
            tripPolyline,
            tripPolyline,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )
    }

    override fun showTraffic() {
        mapFragment?.enableTrafficCommand()
    }

    private fun setupBottomDialog() {

        bottom_dialog.setVisible()

        behavior = BottomSheetBehavior.from(bottom_dialog)

        setupGoogleRouteBottomSheetRecycler()

        // must be called after recycler init
        bottom_dialog.dialogFullyOpenStateChanged(false)

        val statusBarSize = getStatusBarSize()

        setupGoogleRouteBottomSheetCallback(statusBarSize)

        bottom_dialog.route_detail_refill_travel_card_fab.setOnClickListener {
            presenter.onRefillTravelCardFromFabClicked()
        }
    }

    private fun getStatusBarSize(): Int {
        return resources.getDimensionPixelSize(
            resources.getIdentifier(
                "status_bar_height",
                "dimen",
                "android"
            )
        )
    }

    private fun setupGoogleRouteBottomSheetRecycler() {
        bottom_dialog.setupRouteDetailsRecycler(
            { adapterPosition, selectedItemIndex ->
                presenter.onTaxiProviderClick(adapterPosition, selectedItemIndex)
            },
            { presenter.onAgenciesInfoClicked() },
            { presenter.onViewTripOnMapClicked(it) }
        )
    }

    private fun setupGoogleRouteBottomSheetCallback(statusBarSize: Int) {
        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            val initialPadding = resources.getDimensionPixelSize(R.dimen.size_small)

            init {
                if (dragIVPadding != 0) {
                    drag_iv.topPadding =
                        dragIVPadding // восстанавливаем отступ после возврата на экран
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                dragIVPadding = (statusBarSize * slideOffset).toInt() + initialPadding
                drag_iv.topPadding = dragIVPadding
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottom_dialog.backgroundColor = Color.WHITE
                        bottom_dialog.dialogFullyOpenStateChanged(true)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottom_dialog.backgroundDrawable =
                            resources.getDrawable(R.drawable.bg_bottom_dialog, null)
                        bottom_dialog.dialogFullyOpenStateChanged(false)
                    }

                    else -> {
                        bottom_dialog.backgroundDrawable =
                            resources.getDrawable(R.drawable.bg_bottom_dialog, null)
                    }
                }
            }
        })
    }

    private fun drawEachPolyline(
        trips: List<TripViewModel>,
        solidLine: Boolean
    ) {
        trips.forEach {
            val color = if (it is SubwayViewModel) {
                Color.parseColor(it.bgColor)
            } else {
                ContextCompat.getColor(requireContext(), it.tripColor)
            }
            val pattern = if (it is OnFootViewModel) {
                listOf(Dot(), Gap(12f))
            } else {
                if (!solidLine) listOf(Dash(50f), Gap(12f)) else null
            }
            mapFragment?.googleMapHelper?.drawPolyline(
                GoogleMapHelper.PolylineOptions(it.tripPolyline, color, pattern)
            )
        }
    }

    private fun showStartIconOnMap(position: LatLng) {
        mapFragment?.googleMapHelper?.addMarkerOnMap(
            GoogleMapHelper.PinOptions(
                position = position,
                icon = R.drawable.map_pin,
                iconWidth = 30.toPx(),
                iconHeight = 34.toPx()
            ),
            resources
        )
    }

    private fun showEndIconOnMap(position: LatLng) {
        mapFragment?.googleMapHelper?.addMarkerOnMap(
            GoogleMapHelper.PinOptions(
                position = position,
                icon = R.drawable.map_pin_destination,
                iconWidth = 30.toPx(),
                iconHeight = 30.toPx()
            ),
            resources
        )
    }

    private fun changeCurrentLocationIconBottomConstraint(toTopOf: Int) {

        val constraintSet = ConstraintSet()
        constraintSet.clone(map_controls)
        constraintSet.connect(
            R.id.map_current_location,
            ConstraintSet.BOTTOM,
            toTopOf,
            ConstraintSet.TOP,
            resources.getDimensionPixelSize(R.dimen.size_medium)
        )
        constraintSet.applyTo(map_controls)
    }

    private fun showAllRoute(startPolyline: String, endPolyline: String) {
        mapFragment?.googleMapHelper?.fillFullPolylineIntoMap(
            startPolyline,
            endPolyline,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )
    }
}
