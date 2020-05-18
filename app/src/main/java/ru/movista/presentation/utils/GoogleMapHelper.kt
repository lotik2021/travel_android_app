package ru.movista.presentation.utils

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import ru.movista.R
import timber.log.Timber

class GoogleMapHelper(
    fragmentManager: androidx.fragment.app.FragmentManager,
    mapFragmentId: Int,
    private val resources: Resources,
    private var onMapReadyListener: (() -> Unit)? = {}
) {

    companion object {
        private const val DEFAULT_ZOOM = 14f
        private const val MAX_ZOOM = 18f
        private const val DEFAULT_ROTATION = 0F
    }

    private var mapFragment: SupportMapFragment? = null
    private var map: GoogleMap? = null
    private var onMapReadyCallback: OnMapReadyCallback? = null

    private var currentLocationMarker: Marker? = null

    private var currentLocationMarkerIcon = CurrentLocationMarkerIcon.UNDEFINED

    private var accuracyCircle: Circle? = null
    private var polylines: ArrayList<Polyline> = arrayListOf()
    private var markers: ArrayList<Marker> = arrayListOf()

    init {
        mapFragment = fragmentManager.findFragmentById(mapFragmentId) as? SupportMapFragment
        onMapReadyCallback = OnMapReadyCallback { onMapReady(it) }
        mapFragment?.getMapAsync(onMapReadyCallback) ?: Timber.e("mapFragment is null")
    }

    fun clearView() {
        map?.clear()
        currentLocationMarker = null
        currentLocationMarkerIcon = CurrentLocationMarkerIcon.UNDEFINED
    }

    fun clearSelf() {
        map?.setOnCameraIdleListener(null)
        map?.setOnCameraMoveListener(null)
        map?.setOnMapLoadedCallback(null)
        map?.clear()
        map = null
        onMapReadyListener = null
        onMapReadyCallback = null
    }

    fun clearRoute() {
        polylines.forEach { it.remove() }
        polylines.clear()
        markers.forEach { it.remove() }
        markers.clear()
    }

    fun setPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        map?.setPadding(left, top, right, bottom) ?: Timber.e("Map is null")
    }

    fun drawPolyline(options: PolylineOptions) {

        map?.apply {
            val decodedPolyline = PolyUtil.decode(options.polyline)

            val whiteBase = PolylineOptions()
                .addAll(decodedPolyline)
                .startCap(RoundCap())
                .endCap(RoundCap())
                .width(16f)
                .color(Color.WHITE)

            val polyOptions = PolylineOptions()
                .addAll(decodedPolyline)
                .startCap(RoundCap())
                .endCap(RoundCap())
                .pattern(options.pattern)
                .width(resources.getDimensionPixelSize(R.dimen.map_polyline_width).toFloat())
                .color(options.color)

//            addPolyline(whiteBase)
            val drawnPolyline = addPolyline(polyOptions)
            polylines.add(drawnPolyline)

        } ?: Timber.e("Map to draw on is null")
    }

    fun fillFullPolylineIntoMap(firstPolyline: String, lastPolyline: String, width: Int, height: Int) {
        val firstDecodedPolyline = PolyUtil.decode(firstPolyline)
        val lastDecodedPolyline = PolyUtil.decode(lastPolyline)

        val bounds = LatLngBounds
            .Builder()
            .include(firstDecodedPolyline[firstDecodedPolyline.lastIndex])
            .include(lastDecodedPolyline[lastDecodedPolyline.lastIndex])
            .include(firstDecodedPolyline[0])
            .include(lastDecodedPolyline[0])
            .build()
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 64.toPx()))
            ?: Timber.e("Map to move is null")
    }

    fun addMarkerOnMap(pinOptions: PinOptions, resources: Resources) {
        val markerOptions = MarkerOptions()
            .position(pinOptions.position)
            .anchor(pinOptions.anchorX, pinOptions.anchorY)
            .icon(
                BitmapDescriptorFactory
                    .fromBitmap(
                        resizeMapIcons(
                            pinOptions.iconWidth,
                            pinOptions.iconHeight,
                            BitmapFactory.decodeResource(resources, pinOptions.icon)
                        )
                    )
            )
        map?.apply {
            val addedMarker = addMarker(markerOptions)
            markers.add(addedMarker)
        } ?: Timber.e("Map to add marker on on is null")

    }

    fun moveCamera(lat: Double, lon: Double, maxZoom: Boolean = false) {
        stopMapRotating()
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(lat, lon),
                if (maxZoom) MAX_ZOOM else DEFAULT_ZOOM
            )
        )
    }

    fun animateCamera(lat: Double, lon: Double, maxZoom: Boolean = false) {
        stopMapRotating()
        map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(lat, lon),
                if (maxZoom) MAX_ZOOM else DEFAULT_ZOOM
            )
        )
    }

    fun setOnMapIdleListener(listener: (Double, Double) -> Unit) {
        map?.setOnCameraIdleListener {
            val target = map?.cameraPosition?.target
            target?.let { listener.invoke(it.latitude, it.longitude) }
        }
    }

    fun setOnCameraMovedListener(listener: () -> Unit) {
        map?.setOnCameraMoveStartedListener { reason ->
            when (reason) {
                GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> listener.invoke()
                else -> {
                    // do nothing
                }
            }

        }
    }

    fun zoomIn() {
        map?.animateCamera(CameraUpdateFactory.zoomIn())
    }

    fun zoomOut() {
        map?.animateCamera(CameraUpdateFactory.zoomOut())
    }

    @SuppressLint("MissingPermission")
    fun setMyLocationEnabled() {
        map?.isMyLocationEnabled = true
    }

    fun changeCurrentLocationPosition(lat: Double, lon: Double) {
        Timber.d("changing CurrentLocationPosition")
        if (currentLocationMarker == null) {
            Timber.i("currentLocationMarker is null - creating a new one")

            currentLocationMarker = map?.addMarker(
                MarkerOptions()
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_current_location))
                    .position(LatLng(lat, lon))
                    .zIndex(1f) // по стандарту 0.0 и располагаются по принципу первая на самом верху
            )

            currentLocationMarkerIcon =
                CurrentLocationMarkerIcon.WITHOUT_DIRECTION
        } else {
            Timber.d("moving currentLocationMarker to new position")
            currentLocationMarker?.position = LatLng(lat, lon)
        }
    }

    fun drawAccuracyCircle(latLng: LatLng, radius: Double, resources: Resources) {
        accuracyCircle?.remove()

        accuracyCircle = map?.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeWidth(0f)
                .fillColor(ResourcesCompat.getColor(resources, R.color.map_accuracy_circle, null))
        )
    }

    fun changeCurrentLocationOrientation(bias: Float) {

        // уже должно показываться текущее местоположение
        currentLocationMarker ?: return

        if (currentLocationMarkerIcon == CurrentLocationMarkerIcon.WITHOUT_DIRECTION) {
            Timber.i("changing currentLocationMarkerIcon -> with direction")
            currentLocationMarker?.apply {
                setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_location_with_direction))
                setAnchor(0.5f, 0.67f)
                rotation = bias

                currentLocationMarkerIcon =
                    CurrentLocationMarkerIcon.WITH_DIRECTION
            }
        } else {
            currentLocationMarker?.let { it.rotation = bias }
        }
    }

    fun hideCurrentLocation() {
        currentLocationMarker?.remove()
        currentLocationMarker = null
        currentLocationMarkerIcon = CurrentLocationMarkerIcon.UNDEFINED
    }

    fun hideOrientation() {
        // уже должно показываться текущее местоположение
        currentLocationMarker ?: return

        if (currentLocationMarkerIcon == CurrentLocationMarkerIcon.WITH_DIRECTION) {
            Timber.i("changing currentLocationMarkerIcon -> without direction")
            currentLocationMarker?.apply {
                setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_current_location))
                setAnchor(0.5f, 0.67f)
                rotation = DEFAULT_ROTATION

                currentLocationMarkerIcon = CurrentLocationMarkerIcon.WITHOUT_DIRECTION
            }
        }
    }


    fun stopMapRotating() {
        changeMapOrientation(DEFAULT_ROTATION)
    }

    fun changeMapOrientation(bias: Float) {
        val oldPs = map?.cameraPosition

        val newPos = CameraPosition.builder(oldPs).bearing(bias).build()

        map?.animateCamera(CameraUpdateFactory.newCameraPosition(newPos))
    }

    fun showTraffic() {
        map?.isTrafficEnabled = true
    }

    fun hideTraffic() {
        map?.isTrafficEnabled = false
    }

    private fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap == null) {
            Timber.e("getMapAsync return null")
            return
        }
        map = googleMap
        applyDefaultMapStyleParams()

        map?.setOnMapLoadedCallback {
            onMapReadyListener?.invoke() ?: Timber.e("onMapReadyListener is null")
        }
    }

    private fun applyDefaultMapStyleParams() {
        map?.apply {

            setOnMapClickListener { /*иначе по клику на логотип открывается приложение гугл карт*/ }

            uiSettings.isMapToolbarEnabled = false
            uiSettings.isTiltGesturesEnabled = false
            uiSettings.isCompassEnabled = false
            uiSettings.isRotateGesturesEnabled = true
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isIndoorLevelPickerEnabled = false
        } ?: Timber.e("Map is null")
    }

    private fun resizeMapIcons(width: Int, height: Int, bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    data class PolylineOptions(
        val polyline: String,
        val color: Int,
        val pattern: List<PatternItem>? = null
    )

    data class PinOptions(
        val position: LatLng,
        val icon: Int,
        val iconWidth: Int,
        val iconHeight: Int,
        val anchorX: Float = 0.5f,
        val anchorY: Float = 1f
    )

    private enum class CurrentLocationMarkerIcon {
        WITH_DIRECTION, WITHOUT_DIRECTION, UNDEFINED
    }
}