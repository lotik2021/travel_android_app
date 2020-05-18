package ru.movista.data.repository

import io.reactivex.Single
import ru.movista.data.entity.EmptyResponse
import ru.movista.data.entity.OnesignalPlayerRequest
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class OneSignalRepository @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val api: Api
) {
    companion object {
        private const val USER_ID_ONE_SIGNAL = "user_id_one_signal"
    }

    fun setOsPlayerId(deviceId: String, userIdOneSignal: String): Single<EmptyResponse> {
        return api.setOsPlayerId(
            keyValueStorage.getToken(),
            OnesignalPlayerRequest(deviceId, userIdOneSignal)
        )
    }

    fun isUserAuthorizedInOneSignal(): Boolean {
        return keyValueStorage.getString(USER_ID_ONE_SIGNAL, null) != null
    }

    fun saveUserAuthorizedInOneSignal(userIdOneSignal: String) {
        keyValueStorage.putString(USER_ID_ONE_SIGNAL, userIdOneSignal)
    }
}