package ru.movista.domain.model.tickets

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime
import ru.movista.data.source.local.models.*

@Parcelize
data class PathGroup(
    val id: String,
    val title: String?,
    val description: String?,
    val minDuration: String?,
    val minDurationTitle: String?,
    val minPrice: Double?,
    val minPriceTitle: String?,
    val segments: List<Segment> = emptyList(),
    val tripTypeSequence: List<List<TripType>> = emptyList(),
    val routesCount: Long?,
    val tripPlaces: Map<Long, TripPlace> = emptyMap()
) : Parcelable

@Parcelize
data class Segment(
    val id: String,
    val name: String,
    val fromId: Long,
    val toId: Long,
    val isReturn: Boolean,
    var routes: MutableList<Route> = mutableListOf(),
    val tripTypes: List<TripType>,
    var selectedRoute: Route? = null, // local
    var basketItemState: BasketItemState = BasketItemState.NOT_SELECTED // local
) : Parcelable {
    fun isOnlyTaxi() : Boolean {
        return this.tripTypes.size == 1 && this.tripTypes.first() == TripType.TAXI
    }
}

@Parcelize
data class Route(
    val id: String,
    val totalRoutePrice: Double,
    val priceSegmentType: PriceSegmentType,
    val tripsToServices: List<TripToService>,
    val description: String,
    val minAgrPrice: Double,
    val price: Double,
    val routeDuration: Long,
    val isForward: Boolean,
    val transferCount: Int,
    val departureDate: ZonedDateTime?,
    val arrivalDate: ZonedDateTime?,
    val totalDuration: Long, // todo local, сумма всех duration в trip, удалить переменную, когда исправият баг на сервере и будет приходить routeDuration
    val fromDescription: String?,
    val toDescription: String?
) : Parcelable

@Parcelize
data class TripToService(
    val trip: Trip,
    val service: RoutService
) : Parcelable

@Parcelize
data class RoutService(
    val id: String?,
    val objectType: ObjectServiceType,
    val providerServiceCode: String?,
    val alternatives: List<Alternative>?,
    val flightType: String?,
    val bookingUrl: String?,
    val bookingUrlCaption: String?,
    val serviceSegmentOption: ServiceSegmentOption?,
    var selectedAlternative: Alternative? = null // local
) : Parcelable

@Parcelize
data class ServiceSegmentOption(
    val fareFamilyName: String?,
    val comfortType: ComfortType?,
    val freeSeatsCount: Int?,
    val optionsTypes: ServiceOptionTypes?
) : Parcelable

@Parcelize
data class ServiceOptionTypes(
    val baggage: ServiceOptionInfo?,
    val cabin: ServiceOptionInfo?,
    val refundable: ServiceOptionInfo?,
    val exchangeable: ServiceOptionInfo?,
    val miles: ServiceOptionInfo?,
    val seatsRegistration: ServiceOptionInfo?,
    val vipService: ServiceOptionInfo?,
    val meal: ServiceOptionInfo?
) : Parcelable

@Parcelize
data class ServiceOptionInfo(
    val serviceOptionInfoType: ServiceOptionInfoType,
    val count: Int?,
    val weight: Int?,
    val measure: String?,
    val description: String?
) : Parcelable

@Parcelize
data class Alternative(
    val freeSeatsCount: Long?,
    val price: Double?,
    val fee: Double?,
    val currencyCode: String?,
    val priceStatus: String?,
    val coachType: TrainCoachType?
) : Parcelable

@Parcelize
data class Trip(
    val objectType: ObjectTripType,
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
    val fromDescription: String?,
    val toId: Long?,
    val toDescription: String?,
    val transportClass: String?,
    val departure: ZonedDateTime?,
    val arrival: ZonedDateTime?,
    val duration: Long?,
    val carTypeCode: String?,
    val carTypeName: String?,
    val carrierName: String?,
    val isReturnTrip: Boolean?,
    val description: String?,
    val tripType: TripType,
    val boardingDuration: Long?,
    val distance: Long?,
    val unboardingDuration: Long?,
    val isTransfer: Boolean?,
    val title: String?,
    val options: TripOptions?,
    val hasTwoStoreyCoaches: Boolean?,
    val hasElectronicRegistration: Boolean?
) : Parcelable

@Parcelize
data class TripOptions(
    val tv: String?,
    val bioToilet: String?,
    val airConditioning: String?,
    val baggage: String?,
    val refundable: String?
) : Parcelable

@Parcelize
data class TripPlace(
    val id: Long,
    val name: String?,
    val lat: Double?,
    val lon: Double?,
    val timeZone: String?,
    val countryName: String?,
    val stateName: String?,
    val cityName: String?,
    val stationName: String?,
    val platformName: String?,
    val description: String?,
    val fullName: String?
) : Parcelable