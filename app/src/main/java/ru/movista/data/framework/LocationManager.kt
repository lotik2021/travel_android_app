package ru.movista.data.framework

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.movista.data.entity.LastLocationEvent
import ru.movista.data.entity.LastLocationEventFailure
import ru.movista.data.entity.LastLocationEventSuccess
import timber.log.Timber

class LocationManager(val context: Context) : LocationCallback() {

    companion object {
        const val ERROR_NO_LOCATION = "no_location"

        private const val LOG_TAG = "LocationManager"

        private const val PING_ACCURACY = 60000L // 1 минута
        private const val HIGH_ACCURACY = 5000L // 5 сек
    }

    private var lastLocationSubject = PublishSubject.create<LastLocationEvent>()

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        Timber.tag(LOG_TAG).d("onLocationResult")

        val lastLocation = locationResult?.lastLocation ?: return

        Timber.tag(LOG_TAG).d("onLocationResult: received correct location")

        lastLocationSubject.onNext(
            LastLocationEventSuccess(
                lastLocation.latitude,
                lastLocation.longitude,
                lastLocation.accuracy
            )
        )
    }

    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getLasLocationObservable(): Observable<LastLocationEvent> {
        return lastLocationSubject
    }

    @SuppressLint("MissingPermission")
    fun requestLastKnownLocation() {
        Timber.tag(LOG_TAG).d("getting last known location")
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                if (it == null) {
                    Timber.tag(LOG_TAG).d("lastLocation is null")
                    // выключена локация/новое устройство/нет google сервисов
                    stopLocationUpdates()
                    emitCommonFailure()
                } else {
                    lastLocationSubject.onNext(
                        LastLocationEventSuccess(
                            it.latitude,
                            it.longitude,
                            it.accuracy
                        )
                    )
                }
            }
    }

    fun listenToLocationUpdatesIfPossible(highAccuracy: Boolean = false) {
        Timber.tag(LOG_TAG)
            .i("listenToLocationUpdatesIfPossible with highAccuracy set to $highAccuracy")

        val builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(createLocationRequest(highAccuracy))
        val client = LocationServices.getSettingsClient(context)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                // начинаем получать локацию, есть локация доступна на устройстве
                startLocationUpdates(highAccuracy)
            }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(highAccuracy: Boolean) {
        Timber.tag(LOG_TAG).i("startLocationUpdates")

        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(highAccuracy),
            this,
            null /* Looper */
        )
    }

    fun stopLocationUpdates() {
        Timber.tag(LOG_TAG).i("stopLocationUpdates")
        fusedLocationClient.removeLocationUpdates(this)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocationSynchroniusly(): Pair<Double, Double>? {
        return try {
            val androidLocationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager

            val bestProvider = androidLocationManager.getBestProvider(Criteria(), true)

            val location: Location? = androidLocationManager.getLastKnownLocation(bestProvider)

            if (location != null) {
                location.latitude to location.longitude
            } else {
                null
            }

        } catch (e: Exception) {
            Timber.tag(LOG_TAG).d(e)
            null
        }
    }

    private fun createLocationRequest(highAccuracy: Boolean): LocationRequest {

        return LocationRequest.create().apply {
            interval = if (highAccuracy) HIGH_ACCURACY else PING_ACCURACY
            fastestInterval = if (highAccuracy) HIGH_ACCURACY else PING_ACCURACY
            priority =
                if (highAccuracy) LocationRequest.PRIORITY_HIGH_ACCURACY else LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    private fun emitCommonFailure() {
        Timber.tag(LOG_TAG).i("emitCommonFailure")
        lastLocationSubject.onNext(LastLocationEventFailure(ERROR_NO_LOCATION))
    }
}