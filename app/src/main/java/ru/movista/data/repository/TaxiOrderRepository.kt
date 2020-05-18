package ru.movista.data.repository

import io.reactivex.Completable
import ru.movista.data.entity.TaxiOrderReportRequest
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class TaxiOrderRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage
) {

    fun reportTaxiOrder(deeplinkId: String, taxiProviderLink: String): Completable {
        return api.reportTaxiOrder(
            keyValueStorage.getToken(),
            TaxiOrderReportRequest(used_link = taxiProviderLink, id = deeplinkId)
        )
    }
}