package ru.movista.data.entity.tickets.common

import ru.movista.data.source.local.models.ObjectServiceType
import ru.movista.data.source.local.models.TrainCoachType
import ru.movista.domain.model.tickets.ComfortType

class ServicesProp(
    val objectType: ObjectServiceType,
    val serviceBusByPartnerSearch: RoutServiceEntity?,
    val serviceBusOwnSearch: RoutServiceEntity?,
    val serviceNotForSaleSearch: RoutServiceEntity?,
    val serviceTrainOwnSearch: RoutServiceEntity?,
    val serviceFlightFareFamOwnSearch: RoutServiceEntity?,
    val serviceFlightOwnSearch: RoutServiceEntity?,
    val serviceTrainByPartnerSearch: RoutServiceEntity?,
    val serviceTrainUfsSearch: RoutServiceEntity?
)

class RoutServiceEntity(
    val id: String?,
    val bookingUrl: String?,
    val bookingUrlCaption: String?,
    val providerServiceCode: String?,
    val alternatives: List<AlternativeEntity>?,
    val flightType: String?,
    val segments: Map<String, ServiceSegmentOptionEntity>? = null
)

class AlternativeEntity(
    val freeSeatsCount: Long?,
    val price: Double?,
    val fee: Double?,
    val currencyCode: String?,
    val priceStatus: String?,
    val coachType: TrainCoachType?
)

class ServiceSegmentOptionEntity(
    val fareFamilyName: String?,
    val comfortType: ComfortType?,
    val freeSeatsCount: Int?,
    val options: ServiceOptionTypesEntity?
)

class ServiceOptionTypesEntity(
    val baggage: OptionItemEntity?,
    val cabin: OptionItemEntity?,
    val refundable: OptionItemEntity?,
    val exchangeable: OptionItemEntity?,
    val miles: OptionItemEntity?,
    val seatsRegistration: OptionItemEntity?,
    val vipService: OptionItemEntity?,
    val meal: OptionItemEntity?
)

class OptionItemEntity(
    val unknown: List<OptionInfoEntity>?,
    val free: List<OptionInfoEntity>?,
    val pay: List<OptionInfoEntity>?,
    val notAvailable: List<OptionInfoEntity>?
)

class OptionInfoEntity(
    val count: Int?,
    val weight: Int?,
    val measure: String?,
    val description: String?
)