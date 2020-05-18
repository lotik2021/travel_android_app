package ru.movista.presentation.converter

import android.content.res.Resources
import android.location.Address
import ru.movista.di.FragmentScope
import ru.movista.presentation.viewmodel.AddressViewModel
import javax.inject.Inject

@FragmentScope
class AddressConverter @Inject constructor(val resources: Resources) {

    companion object {
        private const val PLACE_UNNAMED = "Unnamed"
    }

    fun addressToAddressViewModel(addresses: List<Address>?): AddressViewModel {
        var addressText = "Точка на карте"
        var countryName = ""
        var cityName = ""

        if (addresses != null && addresses.isNotEmpty()) {

            val address = addresses[0]

            addressText = when {
                address.thoroughfare.isOK() && address.featureName.isOK() -> {
                    if (address.thoroughfare != address.featureName) {
                        "${address.thoroughfare}, ${address.featureName}"
                    } else {
                        address.thoroughfare
                    }
                }
                address.thoroughfare.isOK() -> {
                    address.thoroughfare
                }
                address.locality.isOK() -> {
                    address.locality
                }
                else -> addressText
            }

            cityName = when {
                // если для адреса установили значение locality - увеличиваем охват
                addressText != address.locality && address.locality.isOK() -> address.locality
                address.subLocality.isOK() -> address.subLocality
                address.adminArea.isOK() -> address.adminArea
                else -> ""
            }
            if (address.countryName.isOK()) countryName = address.countryName
        }

        return AddressViewModel(
            countryName,
            cityName,
            addressText
        )
    }

    private fun String?.isOK(): Boolean {
        return this != null && !this.contains(PLACE_UNNAMED)
    }
}