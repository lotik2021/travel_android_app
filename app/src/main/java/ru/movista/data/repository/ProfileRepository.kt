package ru.movista.data.repository

import io.reactivex.Single
import ru.movista.data.entity.*
import ru.movista.data.mapper.ProfileMapper
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.ActivityScope
import ru.movista.domain.model.ProfileTransportType
import ru.movista.domain.model.UserProfile
import javax.inject.Inject

@ActivityScope
class ProfileRepository @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val api: Api,
    private val profileMapper: ProfileMapper
) {
    companion object {
        const val USER_IS_AUTHORIZED = "user_is_authorized"
        const val USER_IS_REGISTERED = "user_is_registered"

        const val USER_NAME = "user_name"
        const val USER_LAST_NAME = "user_last_name"
    }

    fun loadProfile(): Single<UserProfile> {
        return api
            .getUserProfile(keyValueStorage.getToken())
            .map { profileMapper.toUserFavoritePlaces(it) }
    }

    fun deleteUserFavoritesPlaces(placeId: Long): Single<EmptyResponse> {
        return api.deleteUserFavoritesPlaces(keyValueStorage.getToken(), placeId)
    }

    fun createUserFavoritesPlaces(request: UserFavoritePlacesRequest): Single<CreateUserFavoritesResponse> {
        return api.createFavoritesPlaces(keyValueStorage.getToken(), request)
    }

    fun updateFavoritesPlaces(placeId: Long, request: UserFavoritePlacesRequest): Single<EmptyResponse> {
        return api.updateFavoritesPlaces(keyValueStorage.getToken(), placeId, request)
    }

    fun updateUserTransportTypes(list: List<ProfileTransportType>): Single<UpdateUserTransportsResponse> {
        val request = UpdateUserTransportsRequest(
            toRequest(list)
        )
        return api.updateUserTransportTypes(keyValueStorage.getToken(), request)
    }

    fun isAuthUser() = keyValueStorage.getBoolean(USER_IS_AUTHORIZED, false)

    fun getUserName(): String = keyValueStorage.getString(USER_NAME, "Имя") ?: "Имя"
    fun getUserMidName(): String = keyValueStorage.getString(USER_LAST_NAME, "Фамилия") ?: "Фамилия"

    fun saveUserName(name: String) = keyValueStorage.putString(USER_NAME, name)
    fun saveUserLastName(lastName: String) = keyValueStorage.putString(USER_LAST_NAME, lastName)

    fun setUserIsAuthorized(isAuthorized: Boolean) = keyValueStorage.putBoolean(USER_IS_AUTHORIZED, isAuthorized)

    fun isUserAuthorized() = keyValueStorage.getBoolean(USER_IS_AUTHORIZED, false)

    fun setUserIsRegistered(isRegistered: Boolean) = keyValueStorage.putBoolean(USER_IS_REGISTERED, isRegistered)

    fun isUserRegistered() = keyValueStorage.getBoolean(USER_IS_REGISTERED, false)

    private fun toRequest(list: List<ProfileTransportType>): List<UpdateUserTransportsListRequest> {
        val result = arrayListOf<UpdateUserTransportsListRequest>()
        list.forEach {
            result.add(UpdateUserTransportsListRequest(it.isOn, it.id))
        }
        return result
    }
}