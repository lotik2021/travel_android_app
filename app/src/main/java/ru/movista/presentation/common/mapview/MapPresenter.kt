package ru.movista.presentation.common.mapview

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.movista.data.entity.LastLocationEventSuccess
import ru.movista.data.framework.LocationManager
import ru.movista.data.framework.OrientationManager
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class MapPresenter : BasePresenter<MapView>() {

    companion object {
        private const val ACCURACY_TO_SHOW_THRESHOLD = 28f
    }

    override val screenTag: String
        get() = Screens.Map.TAG

    private var mapIsReady = false

    private var currentLocationRequestedByUser = false

    private var mapOrientationEnabled = false

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private var trafficEnabled = false

    @Inject
    lateinit var orientationManager: OrientationManager

    @Inject
    lateinit var locationManager: LocationManager

    override fun onPresenterInject() {
        super.onPresenterInject()

        Injector.init(screenTag)
        Injector.mapComponent?.inject(this)
    }

    fun onResume() {
        if (mapIsReady) {
            locationManager.startLocationUpdates(true)
        }
    }

    fun onPause() {
        locationManager.stopLocationUpdates()
        orientationManager.stopListenToOrientationChanges()
    }

    fun onCurrentLocationClicked() {
        currentLocationRequestedByUser = true

        mapOrientationEnabled = false
        disableMapOrientation()

        getCurrentLocation()
    }


    fun onOrientationClicked() {
        mapOrientationEnabled = !mapOrientationEnabled

        if (mapOrientationEnabled) {
            viewState.changeOrientationIcon(true)

            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                viewState.moveMap(
                    lat = currentLatitude,
                    lon = currentLongitude,
                    withAnimation = false,
                    withMaxZoom = true
                )
            } else {
                listenToOrientationChanges()
            }
        } else {
            disableMapOrientation()
        }
    }

    fun onTrafficClicked() {
        trafficEnabled = !trafficEnabled
        if (trafficEnabled) {
            viewState.showTraffic()
        } else {
            viewState.hideTraffic()
        }
    }

    fun onMapReady() {
        mapIsReady = true
        listenToLocationChanges()
    }

    fun onZoomInClicked() {
        viewState.zoomIn()
    }

    fun onZoomOutClicked() {
        viewState.zoomOut()
    }

    fun onEnableTrafficCommandReceived() {
        trafficEnabled = true
        viewState.showTraffic()
    }

    fun onStopMapRotatingCommandReceived() {
        mapOrientationEnabled = false
        disableMapOrientation()
    }

    private fun listenToLocationChanges() {
        addDisposable(
            locationManager.getLasLocationObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { locationManager.listenToLocationUpdatesIfPossible(true) }
                .subscribe(
                    {
                        when (it) {
                            is LastLocationEventSuccess -> {
                                Timber.d("Last location received")

                                changeCurrentLocation(it)
                                // можем начать следить за изменениями ориентации устройства
                                listenToOrientationChanges()
                            }
                            else -> {
                            }
                        }
                    },
                    {}
                )

        )
    }

    private fun changeCurrentLocation(it: LastLocationEventSuccess) {
        currentLatitude = it.lat
        currentLongitude = it.lon

        if (currentLocationRequestedByUser) {
            viewState.moveMap(
                lat = currentLatitude,
                lon = currentLongitude,
                withAnimation = true,
                withMaxZoom = true
            )
            currentLocationRequestedByUser = false
        }

        viewState.changeCurrentLocationPosition(
            currentLatitude,
            currentLongitude
        )

        if (it.accuracy > ACCURACY_TO_SHOW_THRESHOLD) {
            viewState.drawAccuracy(currentLatitude, currentLongitude, it.accuracy.toDouble())
        }
    }


    private fun listenToOrientationChanges() {
        // выходим из фунции, если уже следим за изменениями ориентации устройства
        if (orientationManager.isListeningToOrientationChanges) return

        val orientationObservable = orientationManager
            .startListenToOrientationChangesIfPossible()

        orientationObservable?.let { observable ->
            addDisposable(
                observable
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when {
                            it.isOnNext -> {
                                val newOrientationValue = it.value
                                newOrientationValue?.let {
                                    viewState.changeCurrentLocationOrientation(
                                        newOrientationValue, mapOrientationEnabled
                                    )
                                }
                            }
                            else -> {
                            }
                        }
                    }
            )
        }
    }

    private fun getCurrentLocation() {
        if (locationManager.isLocationPermissionGranted()) {
            locationManager.requestLastKnownLocation()
        } else {
            viewState.requestLocationPermission()
        }
    }

    private fun disableMapOrientation() {
        viewState.changeOrientationIcon(false)
        viewState.stopMapRotating()
    }
}