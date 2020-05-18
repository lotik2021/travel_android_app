package ru.movista.data.mapper

import ru.movista.data.entity.GetUserProfileResponse
import ru.movista.data.entity.UserFavoritePlacesResponse
import ru.movista.data.entity.UserProfileTransportTypesResponse
import ru.movista.di.ActivityScope
import ru.movista.domain.model.*
import javax.inject.Inject

@ActivityScope
class ProfileMapper @Inject constructor() {

    fun toUserFavoritePlaces(response: GetUserProfileResponse): UserProfile {
        var homePlace: FavoritePlace? = null
        var workPlace: FavoritePlace? = null
        val otherFavorites = arrayListOf<FavoritePlace>()

        response.favorite_places.forEach {
            when (it.favorite_place_type) {
                "home" -> homePlace = toFavoritePlaces(it)
                "work" -> workPlace = toFavoritePlaces(it)
                else -> otherFavorites.add(toFavoritePlaces(it))
            }
        }

        val favoritesPlaces = UserFavoritesPlaces(
            homePlace = homePlace,
            workPlace = workPlace,
            otherFavoritesPlaces = otherFavorites
        )

        return UserProfile(
            name = response.name,
            lastName = response.last_name,
            userFavoritesPlaces = favoritesPlaces,
            googleTransports = toTransportTypes(response.google_transports)
        )
    }

    private fun toFavoritePlaces(favoritePlace: UserFavoritePlacesResponse): FavoritePlace {
        val favoritePlacesAddress = FavoritePlaceAddress(
            placeName = favoritePlace.google_place.main_text,
            placeDescription = favoritePlace.google_place.secondary_text
        )

        return FavoritePlace(
            id = favoritePlace.id,
            name = favoritePlace.name,
            placeAddress = favoritePlacesAddress,
            updatedAt = favoritePlace.updated_at,
            favoritePlaceType = FavoritePlaceType.getPlaceType(favoritePlace.favorite_place_type),
            types = favoritePlace.google_place.types,
            icon = favoritePlace.google_place.android_icon_url,
            iosIcon = favoritePlace.google_place.ios_icon_url_dark
        )
    }

    private fun toTransportTypes(transports: List<UserProfileTransportTypesResponse>): List<ProfileTransportType> {
        val result = arrayListOf<ProfileTransportType>()
        transports.forEach {
            result.add(
                ProfileTransportType(
                    id = it.google_transport,
                    title = it.transport_name,
                    icon = it.android_icon_url,
                    iosIcon = it.ios_icon_url,
                    isOn = it.filter_status
                )
            )
        }
        return result
    }
}