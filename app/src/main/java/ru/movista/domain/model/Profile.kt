package ru.movista.domain.model

import java.io.Serializable

data class UserProfile(
    val name: String?,
    val lastName: String?,
    val googleTransports: List<ProfileTransportType>,
    val userFavoritesPlaces: UserFavoritesPlaces
) : Serializable

data class UserFavoritesPlaces(
    val otherFavoritesPlaces: List<FavoritePlace>,
    val homePlace: FavoritePlace?,
    val workPlace: FavoritePlace?
) : Serializable

data class FavoritePlace(
    val id: Long,
    val name: String,
    val favoritePlaceType: FavoritePlaceType,
    val updatedAt: String = "",
    val placeAddress: FavoritePlaceAddress,
    val types: List<String>?,
    val icon: String,
    val iosIcon: String
) : Serializable

data class FavoritePlaceAddress(
    val placeName: String,
    val placeDescription: String
) : Serializable

data class ProfileTransportType(
    val id: String,
    val title: String,
    val icon: String,
    val iosIcon: String,
    var isOn: Boolean
) : Serializable

enum class FavoritePlaceType(val title: String) : Serializable {
    HOME("home"),
    WORK("work"),
    NONE("none");

    companion object {
        fun getPlaceType(title: String): FavoritePlaceType {
            values().forEach {
                if (it.title == title) {
                    return it
                }
            }
            return NONE
        }
    }
}