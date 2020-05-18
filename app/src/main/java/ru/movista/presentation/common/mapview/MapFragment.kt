package ru.movista.presentation.common.mapview

import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.view_map.*
import moxy.presenter.InjectPresenter
import org.jetbrains.anko.image
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnMapEventsListener
import ru.movista.presentation.main.MainActivity
import ru.movista.presentation.utils.GoogleMapHelper
import ru.movista.utils.reportNullFieldError

class MapFragment : BaseFragment(), MapView {

    @InjectPresenter
    lateinit var presenter: MapPresenter

    var googleMapHelper: GoogleMapHelper? = null

    override fun getLayoutRes() = R.layout.view_map

    override fun initUI() {
        map_zoom_in.setOnClickListener {
            presenter.onZoomInClicked()
            notifyParentOnZoomClicked()
            notifyParentOnZoomInClicked()
        }
        map_zoom_out.setOnClickListener {
            presenter.onZoomOutClicked()
            notifyParentOnZoomClicked()
            notifyParentOnZoomOutClicked()
        }
        map_current_location.setOnClickListener {
            presenter.onCurrentLocationClicked()
            notifyParentOnCurrentLocationClicked()
        }
        map_orientation.setOnClickListener {
            presenter.onOrientationClicked()
            notifyParentOnOrientationClicked()
        }
        map_traffic.setOnClickListener { presenter.onTrafficClicked() }
    }

    override fun afterCreate() {
        super.afterCreate()

        if (googleMapHelper == null) {
            googleMapHelper = GoogleMapHelper(childFragmentManager, R.id.map_fragment, resources) {
                googleMapHelper?.setPadding(bottom = resources.getDimensionPixelSize(R.dimen.е96))
                presenter.onMapReady()
                notifyParentOnMapReady()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleMapHelper?.clearView()
    }

    override fun onDestroy() {
        googleMapHelper?.clearSelf()
        googleMapHelper = null
        super.onDestroy()
    }

    override fun changeCurrentLocationPosition(lat: Double, lon: Double) {
        googleMapHelper?.changeCurrentLocationPosition(lat, lon)
    }

    override fun changeCurrentLocationOrientation(bias: Float, rotateMap: Boolean) {
        googleMapHelper?.changeCurrentLocationOrientation(bias)

        if (rotateMap) googleMapHelper?.changeMapOrientation(bias)
    }

    override fun hideCurrentLocation() {
        googleMapHelper?.hideCurrentLocation()
    }

    override fun hideOrientation() {
        googleMapHelper?.hideOrientation()
    }

    override fun requestLocationPermission() {
        (activity as? MainActivity)?.requestLocationPermission() ?: reportNullFieldError("activity")
    }

    override fun moveMap(lat: Double, lon: Double, withAnimation: Boolean, withMaxZoom: Boolean) {
        if (withAnimation) {
            googleMapHelper?.animateCamera(lat, lon, withMaxZoom)
        } else {
            googleMapHelper?.moveCamera(lat, lon, withMaxZoom)
        }
    }

    override fun stopMapRotating() {
        googleMapHelper?.stopMapRotating()
    }

    override fun drawAccuracy(latitude: Double, longitude: Double, radius: Double) {
        googleMapHelper?.drawAccuracyCircle(LatLng(latitude, longitude), radius, resources)
    }

    override fun zoomIn() {
        googleMapHelper?.zoomIn()
    }

    override fun zoomOut() {
        googleMapHelper?.zoomOut()
    }

    override fun showTraffic() {
        map_traffic.image =
            resources.getDrawable(R.drawable.ripple_map_control_traffic_enabled, null)

        googleMapHelper?.showTraffic().also {
            notifyParentOnTrafficEnabled()
        }
    }

    override fun hideTraffic() {
        map_traffic.image =
            resources.getDrawable(R.drawable.ripple_map_control_traffic_disabled, null)

        googleMapHelper?.hideTraffic().also {
            notifyParentOnTrafficDisabled()
        }
    }

    override fun changeOrientationIcon(enabled: Boolean) {
        val icon = if (enabled)
            R.drawable.ripple_map_control_orientation_enabled else R.drawable.ripple_map_control_orientation_disabled
        map_orientation.image = resources.getDrawable(icon, null)
    }

    fun enableTrafficCommand() {
        presenter.onEnableTrafficCommandReceived()
    }

    fun stopMapRotatingCommand() {
        presenter.onStopMapRotatingCommandReceived()
    }

    private fun notifyParentOnMapReady() {
        getCurrentOnMapEventsListener()?.onMapReady()
    }

    private fun notifyParentOnZoomClicked() {
        getCurrentOnMapEventsListener()?.onMapZoomClicked()
    }

    private fun notifyParentOnZoomInClicked() {
        getCurrentOnMapEventsListener()?.onMapZoomInClicked()
    }

    private fun notifyParentOnZoomOutClicked() {
        getCurrentOnMapEventsListener()?.onMapZoomOutClicked()
    }

    private fun notifyParentOnTrafficEnabled() {
        getCurrentOnMapEventsListener()?.onMapTrafficEnabled()
    }

    private fun notifyParentOnTrafficDisabled() {
        getCurrentOnMapEventsListener()?.onMapTrafficDisabled()
    }

    private fun notifyParentOnCurrentLocationClicked() {
        getCurrentOnMapEventsListener()?.onMapCurrentLocationClicked()
    }

    private fun notifyParentOnOrientationClicked() {
        getCurrentOnMapEventsListener()?.onMapOrientationClicked()
    }

    private fun getCurrentOnMapEventsListener(): OnMapEventsListener? {
        val parentFragment = parentFragment
        parentFragment ?: return null

        return if (parentFragment is BaseFragment) {
            val parentPresenter = parentFragment.sharePresenter()

            when {
                parentPresenter is OnMapEventsListener -> parentPresenter
                parentFragment is OnMapEventsListener -> parentFragment
                else -> null
            }
        } else {
            null
        }
    }

}