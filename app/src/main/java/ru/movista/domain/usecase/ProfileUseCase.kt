package ru.movista.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import ru.movista.data.entity.ApiError
import ru.movista.data.entity.HistoryLocationResponse
import ru.movista.data.entity.UpdateUserTransportsResponse
import ru.movista.data.entity.UserFavoritePlacesRequest
import ru.movista.data.repository.ProfileRepository
import ru.movista.di.FragmentScope
import ru.movista.domain.model.*
import javax.inject.Inject

@FragmentScope
class ProfileUseCase @Inject constructor(private val profileRepository: ProfileRepository) {

    fun isUserRegistered() = profileRepository.isUserRegistered()

    fun getUserFullName(): String {
        return "${profileRepository.getUserName()} ${profileRepository.getUserMidName()}"
    }

    fun loadProfile(): Single<UserProfile> {
        return profileRepository.loadProfile()
            .doOnSuccess {
                it.name?.let { name -> profileRepository.saveUserName(name) }
                it.lastName?.let { lastName -> profileRepository.saveUserLastName(lastName) }
            }
    }

    fun deleteUserFavoritesPlaces(placeId: Long): Completable {
        return profileRepository
            .deleteUserFavoritesPlaces(placeId)
            .doOnSuccess { result -> throwIfError(result.error) }
            .ignoreElement()
    }

    fun updateUserTransportTypes(list: List<ProfileTransportType>): Single<UpdateUserTransportsResponse> {
        return profileRepository.updateUserTransportTypes(list)
    }

    fun updateFavoritesPlaces(
        place: Place,
        types: List<String>,
        placeType: FavoritePlaceType,
        placeId: Long,
        icon: String,
        iosIcon: String
    ): Single<FavoritePlace> {
        val request = createFavoritePlacesRequest(place, placeType)
        return profileRepository.updateFavoritesPlaces(placeId, request)
            .doOnSuccess { result -> throwIfError(result.error) }
            .map { createFavoritePlace(placeId, request, types, icon, iosIcon) }
    }

    fun createUserFavoritesPlaces(
        place: Place,
        types: List<String>,
        placeType: FavoritePlaceType,
        placeName: String?,
        icon: String,
        iosIcon: String
    ): Single<FavoritePlace> {
        val request = createFavoritePlacesRequest(place, placeType, placeName)
        return profileRepository.createUserFavoritesPlaces(request)
            .doOnSuccess { result -> throwIfError(result.error) }
            .map { response -> createFavoritePlace(response.id, request, types, icon, iosIcon) }
    }

    private fun createFavoritePlace(
        placeId: Long,
        favoritePlacesRequest: UserFavoritePlacesRequest,
        types: List<String>,
        icon: String,
        iosIcon: String
    ): FavoritePlace {
        return FavoritePlace(
            id = placeId,
            name = favoritePlacesRequest.name ?: "",
            favoritePlaceType = FavoritePlaceType.getPlaceType(favoritePlacesRequest.type),
            placeAddress = FavoritePlaceAddress(
                placeName = favoritePlacesRequest.main_text,
                placeDescription = favoritePlacesRequest.secondary_text
            ),
            types = types,
            icon = icon,
            iosIcon = iosIcon
        )
    }

    private fun createFavoritePlacesRequest(
        place: Place,
        placeType: FavoritePlaceType,
        placeName: String? = null
    ): UserFavoritePlacesRequest {
        return UserFavoritePlacesRequest(
            place_id = place.placeId,
            main_text = place.name,
            secondary_text = place.description,
            type = placeType.title,
            name = placeName,
            location = HistoryLocationResponse(
                longitude = place.lon,
                latitude = place.lat
            )
        )
    }

    private fun throwIfError(apiError: ApiError?) {
        if (apiError != null) throw ApiError(apiError.code, apiError.message)
    }
}