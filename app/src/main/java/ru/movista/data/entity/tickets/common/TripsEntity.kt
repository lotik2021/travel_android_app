package ru.movista.data.entity.tickets.common

import org.threeten.bp.ZonedDateTime
import ru.movista.data.source.local.models.ObjectTripType
import ru.movista.data.source.local.models.TripType

class TripsProp(
    val objectType: ObjectTripType,
    val tripBus: TripEntity?,
    val tripFoot: TripEntity?,
    val tripTaxi: TripEntity?,
    val tripTrain: TripEntity?,
    val tripTrainSuburban: TripEntity?,
    val tripFlight: TripEntity?,
    val tripBusTransfer: TripEntity?,
    val tripTrainSuburbanTransfer: TripEntity?
)

class TripEntity(
    val markCarrierCode: String?,
    val markCarrierName: String?,
    val opCarrierCode: String?,
    val carrierId: Long?,
    val comment: String?,
    val status: String?,
    val uniqueTripId: String?,
    val id: String?,
    val number: String?,
    val opCarrierName: String?,
    val direction: String?,
    val fromId: Long?,
    val fromDescr: String?,
    val toId: Long?,
    val toDescr: String?,
    val transportClass: String?,
    val departure: ZonedDateTime?,
    val arrival: ZonedDateTime?,
    val duration: Long?,
    val carTypeCode: String?,
    val carTypeName: String?,
    val carrierName: String?,
    val isReturnTrip: Boolean?,
    val descr: String?,
    val tripType: TripType,
    val boardingDuration: Long?,
    val distance: Long?,
    val unboardingDuration: Long?,
    val isTransfer: Boolean?,
    val title: String?,
    val options: TripOptionsEntity?,
    val hasTwoStoreyCoaches: Boolean?,
    val hasElectronicRegistration: Boolean?
)

class TripOptionsEntity(
    val tv: String?,
    val bioToilet: String?,
    val airConditioning: String?,
    val baggage: String?,
    val refundable: String?
)