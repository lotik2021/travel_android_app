package ru.movista.data.repository

import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import ru.movista.utils.EMPTY
import javax.inject.Inject

@FragmentScope
class AuthRepository @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val api: Api,
    private val deviceInfo: DeviceInfo
) {

    fun authorize(): Single<ResponseBody> {

        return api.getToken(
            mapOf(
                "device_id" to deviceInfo.deviceId,
                "os" to "Android",
                "device_info" to deviceInfo.deviceModel
            )
        ).doOnSuccess {
            keyValueStorage.saveToken(JSONObject(it.string()).getString("token"))
        }
    }

    fun isTokenExists(): Boolean {
        return keyValueStorage.getToken() != String.EMPTY
    }
}