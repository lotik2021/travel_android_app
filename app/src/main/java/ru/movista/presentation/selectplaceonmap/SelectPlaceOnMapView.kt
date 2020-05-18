package ru.movista.presentation.selectplaceonmap

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.presentation.viewmodel.AddressViewModel

interface SelectPlaceOnMapView : MvpView {

    fun requestAddressByLocation(lat: Double, lon: Double, maxResults: Int)

    fun showAddress(address: AddressViewModel)

    fun moveMap(lat: Double, lon: Double, withAnimation: Boolean = false)

    fun subscribeOnMapEvents()

    @StateStrategyType(SkipStrategy::class)
    fun showNoLocationError()

    fun hideAddressView()


    fun stopMapRotating()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideSelectingContainer()
}