package ru.movista.data.repository

import io.reactivex.Single
import org.json.JSONObject
import ru.movista.data.entity.PayWithStrelkaRequest
import ru.movista.data.entity.PayWithTroikaRequest
import ru.movista.data.entity.StrelkaBalanceRequest
import ru.movista.data.entity.StrelkaBalanceResponse
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class RefillTravelCardRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage,
    private val deviceInfo: DeviceInfo
) {
    companion object {
        private const val TROIKA_CARD_NUM = "TROIKA_CARD_NUM"
        private const val STRELKA_CARD_NUM = "STRELKA_CARD_NUM"
    }

    fun refillTroika(cardNum: String, amount: Int): Single<String> {
        val amountSmall = amount * 100 // в копейках для сервера

        return api
            .payWithTroika(
                keyValueStorage.getToken(),
                PayWithTroikaRequest(amountSmall, cardNum, deviceInfo.deviceId)
            )
            .map {
                return@map JSONObject(it.string()).getString("redirect_url")
            }
            .doOnSuccess {
                keyValueStorage.putString(TROIKA_CARD_NUM, cardNum)
            }
    }

    fun getStrelkaFullParams(cardNum: String): Single<StrelkaBalanceResponse> {
        return api.getStrelkaParams(
            keyValueStorage.getToken(),
            StrelkaBalanceRequest(cardNum)
        ).map {
            if (it.error != null) {
                throw it.error
            } else {
                return@map it
            }
        }
    }

    fun refillStrelka(cardNum: String, amount: Int, cardTypeID: String): Single<String> {
        val amountSmall = amount * 100 // в копейках для сервера

        return api
            .payWithStrelka(
                keyValueStorage.getToken(),
                PayWithStrelkaRequest(deviceInfo.deviceId, cardNum, cardTypeID, amountSmall)
            )
            .map {
                return@map JSONObject(it.string()).getString("redirect_url")
            }
            .doOnSuccess {
                keyValueStorage.putString(STRELKA_CARD_NUM, cardNum)
            }
    }

    fun getTroikaCardNum() = keyValueStorage.getString(TROIKA_CARD_NUM)

    fun getStrelkaCardNum() = keyValueStorage.getString(STRELKA_CARD_NUM)
}