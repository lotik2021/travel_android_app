package ru.movista.data.mapper.tickets

import ru.movista.data.entity.tickets.PlaceFoundEntity
import ru.movista.di.FlowScope
import ru.movista.domain.model.tickets.Place
import ru.movista.utils.EMPTY
import javax.inject.Inject

@FlowScope
class PlacesMapper @Inject constructor() {

    fun toPlaces(searchPlacesResult: List<PlaceFoundEntity>): List<Place> {
        val result = arrayListOf<Place>()
        searchPlacesResult.forEach {
            val name = it.name ?: it.countryName
            val description = it.description ?: String.EMPTY
            val iconName = getIconByPlaceType(it.typePlace)

            result.add(Place(it.id, name, description, iconName))
        }
        return result
    }

    private fun getIconByPlaceType(placeType: Int): String {
        return when (placeType) {
            1, 2, 3, 4 -> "google_place_bus"
            301 -> "google_place_airport"
            101, 102, 103, 106 -> "google_place_train"
            510, 511 -> "google_place_village"
            505 -> "google_place_city"
            else -> "google_place_default"
        }
    }
}