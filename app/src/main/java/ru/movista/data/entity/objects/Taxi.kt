package ru.movista.data.entity.objects

import ru.movista.data.entity.FromLocationEntity
import ru.movista.data.entity.ToLocationEntity
import ru.movista.data.source.local.ObjectType

data class TaxiDataEntity(
    val routes: List<TaxiRouteEntity>
)

sealed class TaxiRouteEntity(
    val object_id: String
)

// Пока все поставщики имеют +- одинаковую сигнатуру - парсим в общую модель такси
data class CommonTaxiRouteEntity(
    val data: CommonTaxiDataEntity,
    val id: String
) : TaxiRouteEntity(ObjectType.ROUTE_TAXI)

data class CommonTaxiDataEntity(
    val from_location: FromLocationEntity,
    val to_location: ToLocationEntity,
    val from_address: String,
    val to_address: String,
    val provider_description: String,
    val polyline: String = "",
    val calculation_id: String,
    val start_time: String = "",
    val end_time: String = "",
    val duration: Int?,
    val distance: Int?,
    val fares: List<CommonTaxiFaresDataEntity>,
    val deeplinks: CommonFaresDeeplinksDataEntity,
    val android_icon_url: String,
    val ios_icon_url: String
)

data class CommonTaxiFaresDataEntity(
    val id: String,
    val name: String,
    val price: String
)

data class CommonFaresDeeplinksDataEntity(
    val android: String,
    val android_store: String,
    val guid: String
)
