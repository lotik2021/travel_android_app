package ru.movista.data.repository

import io.reactivex.Single
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import ru.movista.data.entity.*
import ru.movista.data.entity.tickets.AsyncAvailableResponse
import ru.movista.data.framework.LocationManager
import ru.movista.data.source.local.ActionIds
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.local.USER_RESPONSE
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import ru.movista.domain.model.Place
import ru.movista.domain.model.TripTime
import ru.movista.domain.model.UserData
import ru.movista.utils.getCurrentTimeZone
import javax.inject.Inject

@FragmentScope
class ActionsRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage,
    private val deviceInfo: DeviceInfo,
    private val locationManager: LocationManager
) {

    fun createSession(): Single<DialogResponse> {
        return api.postAction(
            keyValueStorage.getToken(),
            ActionRequest(
                createFreshMetaRequest(),
                action_id = ActionIds.CREATE_SESSION,
                session = SessionEntity(
                    0,
                    "",
                    message_date_time = getCurrentDateTimeWithZone()
                ) // проигнорируется на сервере
            )
        )
    }

    fun asyncAvailable(): Single<AsyncAvailableResponse> {
        return api.asyncAvailable(keyValueStorage.getToken())
    }

    fun sendChosenAction(chosenActionId: String, session: SessionEntity): Single<DialogResponse> {
        return api.postAction(
            keyValueStorage.getToken(),
            ActionRequest(
                createFreshMetaRequest(),
                action_id = chosenActionId,
                session = session.copy(message_date_time = getCurrentDateTimeWithZone())
            )
        )
    }

    fun sendChosenActionWithUserData(
        chosenActionId: String,
        session: SessionEntity,
        data: UserData
    ): Single<DialogResponse> {

        val userData: Any = when (data) {
            is Place -> {
                LocationClientEntityRequest(
                    LocationDataEntityRequest(
                        data.lat,
                        data.lon,
                        data.name,
                        data.description,
                        data.placeId
                    )
                )
            }
            is TripTime -> {
                TripTimeClientEntityRequest(
                    TripTimeDataEntityRequest(data.tripTime)
                )
            }
        }

        return api.postAction(
            keyValueStorage.getToken(),
            ActionRequest(
                createFreshMetaRequest(),
                action_id = chosenActionId,
                session = session.copy(message_date_time = getCurrentDateTimeWithZone()),
                client_entities = userData
            )
        )
    }

    fun sendEnteredUserMessage(
        session: SessionEntity,
        message: String
    ): Single<DialogResponse> {
        val request = ActionRequest(
            createFreshMetaRequest(),
            action_id = USER_RESPONSE,
            session = session.copy(message_date_time = getCurrentDateTimeWithZone()),
            user_response = message
        )

        return api.postAction(
            keyValueStorage.getToken(),
            request
        )
    }

    private fun getCurrentDateTimeWithZone(): String {
        // окгругляем до секунд, наносекунды не нужны серверу
        return ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    private fun createFreshMetaRequest(): MetaRequest {
        val lastLocation = locationManager.getCurrentLocationSynchroniusly()

        val timeZone = getCurrentTimeZone()

        return if (lastLocation == null) {
            MetaRequest(
                deviceInfo.deviceId,
                EmptyClientEntityRequest,
                timeZone
            )
        } else {
            val (lastDeviceLatitude, lastDeviceLongitude) = lastLocation
            MetaRequest(
                deviceInfo.deviceId,
                MetaLocationEntityRequest(lastDeviceLatitude, lastDeviceLongitude),
                timeZone
            )
        }
    }

}