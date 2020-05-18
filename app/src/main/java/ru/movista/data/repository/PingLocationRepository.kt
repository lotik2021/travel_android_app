package ru.movista.data.repository

import io.reactivex.Completable
import ru.movista.data.entity.PingLocationRequest
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class PingLocationRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage,
    private val deviceInfo: DeviceInfo
) {
    fun pingLocation(lat: Double, lon: Double): Completable {
        if (keyValueStorage.getToken().isEmpty()) return Completable.complete()

        return api.pingLocation(
            keyValueStorage.getToken(),
            PingLocationRequest(ru.movista.data.entity.LocationRequest(lat, lon), deviceInfo.deviceId)
        )
    }
}
