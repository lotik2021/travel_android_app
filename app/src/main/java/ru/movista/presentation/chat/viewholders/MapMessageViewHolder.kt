package ru.movista.presentation.chat.viewholders

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.item_map_preview_message.view.*
import ru.movista.R
import ru.movista.presentation.chat.MapItem
import ru.movista.presentation.utils.toPx
import timber.log.Timber

class MapMessageViewHolder(view: View) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view),
    OnMapReadyCallback {

    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null

    private var mapIsUpdated: Boolean = false

    private lateinit var currentMapItem: MapItem

    private val pinFromBitmapDescriptor: BitmapDescriptor by lazy {
        BitmapDescriptorFactory
            .fromBitmap(
                resizeMapIcons(
                    30.toPx(),
                    34.toPx(),
                    BitmapFactory.decodeResource(itemView.resources, R.drawable.map_pin)
                )
            )
    }
    private val pinToBitmapDescriptor: BitmapDescriptor by lazy {
        BitmapDescriptorFactory
            .fromBitmap(
                resizeMapIcons(
                    30.toPx(),
                    30.toPx(),
                    BitmapFactory.decodeResource(itemView.resources, R.drawable.map_pin_destination)
                )
            )
    }

    init {
        mapView = itemView.map

        mapView?.apply {
            onCreate(null)
            getMapAsync(this@MapMessageViewHolder)
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        googleMap?.apply {
            uiSettings.isMapToolbarEnabled = false
//            setOnMapClickListener { /*иначе по клику на логотип открывается приложение гугл карт*/ }

            setOnMapLoadedCallback {
                if (!mapIsUpdated) {
                    updateMap()
                }
            }
        }

    }

    fun bind(mapItem: MapItem) {
        currentMapItem = mapItem
        updateMap()
    }

    fun onRecycle() {
        clearMap()
    }

    private fun updateMap() {
        googleMap?.apply {
            // onRecycle не срабатывает, если уходим с экрана с последующим возвратом
            // поэтому при каждой отрисовке сначала очищаем карту
            clear()

            mapIsUpdated = true

            mapType = GoogleMap.MAP_TYPE_NORMAL

            val latTo = currentMapItem.latTo ?: return
            val lonTo = currentMapItem.lonTo ?: return

            val fromPosition = LatLng(latTo, lonTo)
            addToMarkerOnMap(this, fromPosition)

            val latFrom = currentMapItem.latFrom
            val lonFrom = currentMapItem.lonFrom

            // рисуем маршрут если есть точка отправления
            if (latFrom != null && lonFrom != null) {
                val toPosition = LatLng(latFrom, lonFrom)

                addFromMarkerOnMap(this, toPosition)

                addPolyline(
                    PolylineOptions()
                        .add(fromPosition)
                        .add(toPosition)
                        .width(itemView.resources.getDimension(R.dimen.size_xxxsmall))
                        .color(ContextCompat.getColor(itemView.context, R.color.blue))
                )

                moveCamera(
                    CameraUpdateFactory
                        .newLatLngBounds(
                            LatLngBounds
                                .builder()
                                .include(fromPosition)
                                .include(toPosition)
                                .build(),
                            itemView.resources.getDimensionPixelSize(R.dimen.size_small)
                        )
                )

            } else {
                moveCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 14f))
            }

        } ?: Timber.w("map to update is null")
    }

    private fun addFromMarkerOnMap(googleMap: GoogleMap, position: LatLng) {
        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .icon(pinFromBitmapDescriptor)
        )
    }

    private fun addToMarkerOnMap(googleMap: GoogleMap, position: LatLng) {
        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .anchor(0.5f, 0.5f)
                .icon(pinToBitmapDescriptor)
        )
    }

    private fun resizeMapIcons(width: Int, height: Int, bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    private fun clearMap() {
        googleMap?.apply {
            clear()
            mapType = GoogleMap.MAP_TYPE_NONE
        }
    }
}