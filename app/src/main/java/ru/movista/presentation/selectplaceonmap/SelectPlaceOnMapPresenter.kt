package ru.movista.presentation.selectplaceonmap

import android.location.Address
import moxy.InjectViewState
import ru.movista.data.entity.LastLocationEventFailure
import ru.movista.data.entity.LastLocationEventSuccess
import ru.movista.data.framework.LocationManager
import ru.movista.di.Injector
import ru.movista.domain.model.Place
import ru.movista.domain.usecase.PlaceFoundUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.common.OnLocationPermissionResultListener
import ru.movista.presentation.common.OnMapEventsListener
import ru.movista.presentation.converter.AddressConverter
import ru.movista.presentation.viewmodel.AddressViewModel
import ru.movista.utils.round
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SelectPlaceOnMapPresenter(
    private val isSimpleMap: Boolean
) : BasePresenter<SelectPlaceOnMapView>(),
    OnLocationPermissionResultListener,
    OnMapEventsListener {

    companion object {
        private const val MOSCOW_CENTER_LAT = 55.7537
        private const val MOSCOW_CENTER_LON = 37.6198
    }

    override val screenTag: String
        get() = Screens.SelectPlaceOnMap.TAG

    private lateinit var chosenAddress: AddressViewModel

    private var chosenLat: Double = 0.0 // stub
    private var chosenLon: Double = 0.0

    private var placeFoundUseCase: PlaceFoundUseCase? = null

    /**
     * Флаг используется для показа анимации карты только 1 раз после загрузки карты
     */
    private var wasFirstTimeMapMove = false

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var addressConverter: AddressConverter

    override fun onPresenterInject() {
        Injector.selectPlaceOnMapComponent?.inject(this)
        placeFoundUseCase = Injector.selectPlaceOnMapComponent?.getPlaceFoundUseCase()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (isSimpleMap) {
            viewState.hideSelectingContainer()
        }
    }

    override fun onLocationPermissionReceived(lat: Double, lon: Double) {
        viewState.moveMap(lat, lon, true)
    }

    override fun onMapReady() {

        if (locationManager.isLocationPermissionGranted()) {
            requestLastLocation()
        } else {
            viewState.moveMap(MOSCOW_CENTER_LAT, MOSCOW_CENTER_LON, wasFirstTimeMapMove)
            wasFirstTimeMapMove = true
        }

        viewState.subscribeOnMapEvents()
    }

    fun onMapMovedByUser() {
        if (isSimpleMap) {
            return
        }

        viewState.hideAddressView()
    }

    fun onMapPositionChanged(lat: Double, lon: Double) {
        Timber.d("onMapPositionChanged")

        if (isSimpleMap) {
            return
        }

        if (
        // проверяем что локация не совпадает с предыдущей
            chosenLat.round(4) != lat.round(4)
            && chosenLon.round(4) != lon.round(4)
        ) {
            chosenLat = lat
            chosenLon = lon
            viewState.requestAddressByLocation(lat, lon, 1)
        } else {
            if (this::chosenAddress.isInitialized) viewState.showAddress(chosenAddress)
        }
    }

    fun onAddressReceived(addresses: List<Address>?) {
        Timber.d("onAddressReceived")

        if (isSimpleMap) {
            return
        }

        val addressViewModel = addressConverter.addressToAddressViewModel(addresses)
        chosenAddress = addressViewModel


        viewState.showAddress(addressViewModel)
    }

    fun onDoneBtnClicked() {
        val placeDesc = if (chosenAddress.cityName.isEmpty()) {
            chosenAddress.countryName
        } else {
            "${chosenAddress.cityName}, ${chosenAddress.countryName}"
        }
        placeFoundUseCase?.onPlaceFound(
            Place(
                name = chosenAddress.address,
                description = placeDesc,
                lat = chosenLat,
                lon = chosenLon
            )
        )
        router.exit()
    }

    fun onBackClicked() {
        placeFoundUseCase?.onFindCanceled()
        router.exit()
    }

    private fun requestLastLocation() {
        addDisposable(
            locationManager.getLasLocationObservable()
                .doOnSubscribe { locationManager.requestLastKnownLocation() }
                .subscribe(
                    {
                        when (it) {
                            is LastLocationEventSuccess -> {
                                viewState.moveMap(it.lat, it.lon, wasFirstTimeMapMove)
                                wasFirstTimeMapMove = true
                            }
                            is LastLocationEventFailure -> {
                                if (!wasFirstTimeMapMove) {
                                    viewState.moveMap(
                                        MOSCOW_CENTER_LAT,
                                        MOSCOW_CENTER_LON,
                                        wasFirstTimeMapMove
                                    )
                                    wasFirstTimeMapMove = true
                                } else {
                                    // нажали мое местоположение
                                    viewState.showNoLocationError()
                                }
                            }
                        }

                    },
                    {}
                )
        )
    }

}