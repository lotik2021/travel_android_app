package ru.movista.data.mapper.tickets

import ru.movista.data.entity.tickets.PathGroupData
import ru.movista.data.entity.tickets.PathGroupResponse
import ru.movista.data.entity.tickets.SegmentRoutesResponse
import ru.movista.data.entity.tickets.common.*
import ru.movista.data.source.local.models.*
import ru.movista.di.FragmentScope
import ru.movista.domain.model.tickets.*
import javax.inject.Inject

@FragmentScope
class PathGroupMapper @Inject constructor() {

    fun segmentRoutesMap(entity: SegmentRoutesResponse): List<Route> {
        return mapRoute(entity.data.routes, entity.data.trips, entity.data.services)
    }

    fun map(entity: PathGroupResponse): PathGroup {
        return PathGroup(
            id = entity.data.id,
            title = entity.data.title,
            description = entity.data.descr,
            minDuration = entity.data.minDuration,
            minDurationTitle = entity.data.minDurationTitle,
            minPrice = entity.data.minPrice,
            minPriceTitle = entity.data.minPriceTitle,
            segments = mapSegment(entity.data),
            tripTypeSequence = entity.data.tripTypeSequence,
            routesCount = entity.data.routesCount,
            tripPlaces = mapPlaces(entity.data.places ?: emptyMap())
        )
    }

    private fun mapPlaces(entity: Map<Long, PlaceEntity>): Map<Long, TripPlace> {
        val tripPlaces = mutableMapOf<Long, TripPlace>()

        for ((k, v) in entity) {
            val tripPlace = TripPlace(
                id = v.id,
                name = v.name,
                lat = v.lat,
                lon = v.lon,
                timeZone = v.timeZone,
                countryName = v.countryName,
                stateName = v.stateName,
                cityName = v.cityName,
                stationName = v.stationName,
                platformName = v.platformName,
                description = v.description,
                fullName = v.fullName
            )

            tripPlaces[k] = tripPlace
        }

        return tripPlaces
    }

    private fun mapSegment(entity: PathGroupData): List<Segment> {
        val segments = arrayListOf<Segment>()

        entity.segments.forEach { segmentEntity ->
            val segment = Segment(
                id = segmentEntity.id,
                name = segmentEntity.name,
                fromId = segmentEntity.fromId,
                toId = segmentEntity.toId,
                isReturn = segmentEntity.isReturn,
                tripTypes = segmentEntity.tripTypes,
                routes = mapRoute(segmentEntity.routes, entity.trips, entity.services)
            )
            segments.add(segment)
        }

        return segments
    }

    private fun mapRoute(
        routesEntity: List<RouteEntity>,
        trips: Map<String, TripsProp>,
        services: Map<String, ServicesProp>
    ): MutableList<Route> {
        val routes = arrayListOf<Route>()

        for (routeEntity in routesEntity) {
            val tripsToServicesPairs = arrayListOf<Pair<Trip, RoutService?>>()
            val tripsToServices = arrayListOf<TripToService>()

            routeEntity.tripIdToServiceId.forEach { tripIdToServiceId ->
                val trip = mapTrip(trips[tripIdToServiceId.tripId])
                val service = mapService(services[tripIdToServiceId.serviceId], tripIdToServiceId.tripId)

                if (trip != null) {
                    tripsToServicesPairs.add(trip to service)
                }
            }

            val isInvalidTripsToServices = tripsToServicesPairs.any { it.second == null }
                    || tripsToServicesPairs.isEmpty()
                    || tripsToServicesPairs.all { it.first.objectType.isTransfer() }

            if (isInvalidTripsToServices) {
                continue
            }

            tripsToServices.addAll(tripsToServicesPairs.mapNotNull { pair ->
                pair.second?.let { TripToService(pair.first, it) }
            })

            val firstTrip = tripsToServices.first().trip
            val lastTrip = tripsToServices.last().trip

            val route = Route(
                id = routeEntity.id,
                totalRoutePrice = routeEntity.totalRoutePrice,
                priceSegmentType = routeEntity.priceSegmentType,
                tripsToServices = tripsToServices,
                description = routeEntity.descr,
                minAgrPrice = routeEntity.minAgrPrice,
                price = routeEntity.price,
                routeDuration = routeEntity.routeDuration,
                isForward = routeEntity.isForward,
                transferCount = tripsToServices
                    .filter { it.trip.objectType.isNotTransfer() }
                    .count() - 1,
                departureDate = firstTrip.departure,
                arrivalDate = lastTrip.arrival,
                totalDuration = tripsToServices.sumBy { it.trip.duration?.toInt() ?: 0 }.toLong(),
                fromDescription = firstTrip.fromDescription,
                toDescription = lastTrip.toDescription
            )

            routes.add(route)
        }

        return routes
    }

    private fun mapService(servicesEntity: ServicesProp?, tripId: String): RoutService? {
        servicesEntity ?: return null

        val routServiceEntity: RoutServiceEntity? = when (servicesEntity.objectType) {
            ObjectServiceType.BUS_BY_PARTNER -> servicesEntity.serviceBusByPartnerSearch
            ObjectServiceType.BUS_OWN -> servicesEntity.serviceBusOwnSearch
            ObjectServiceType.NOT_FOR_SALE -> servicesEntity.serviceNotForSaleSearch
            ObjectServiceType.TRAIN_OWN -> servicesEntity.serviceTrainOwnSearch
            ObjectServiceType.FLIGHT_FARE_FAM_OWN -> servicesEntity.serviceFlightFareFamOwnSearch
            ObjectServiceType.FLIGHT_OWN -> servicesEntity.serviceFlightOwnSearch
            ObjectServiceType.TRAIN_BY_PARTNER -> servicesEntity.serviceTrainByPartnerSearch
            ObjectServiceType.TRAIN_UFS -> servicesEntity.serviceTrainUfsSearch
            else -> null
        }

        routServiceEntity ?: return null

        return RoutService(
            id = routServiceEntity.id,
            objectType = servicesEntity.objectType,
            providerServiceCode = routServiceEntity.providerServiceCode,
            alternatives = mapAlternatives(routServiceEntity.alternatives),
            flightType = routServiceEntity.flightType,
            bookingUrl = routServiceEntity.bookingUrl,
            serviceSegmentOption = mapSegmentOption(routServiceEntity.segments, tripId),
            bookingUrlCaption = routServiceEntity.bookingUrlCaption
        )
    }

    private fun mapSegmentOption(
        segments: Map<String, ServiceSegmentOptionEntity>?,
        tripId: String
    ): ServiceSegmentOption? {
        segments ?: return null

        val segmentOptionEntity = segments[tripId] ?: return null

        return ServiceSegmentOption(
            fareFamilyName = segmentOptionEntity.fareFamilyName,
            comfortType = segmentOptionEntity.comfortType,
            freeSeatsCount = segmentOptionEntity.freeSeatsCount,
            optionsTypes = mapServiceOptionTypesEntity(segmentOptionEntity.options)
        )
    }

    private fun mapServiceOptionTypesEntity(
        options: ServiceOptionTypesEntity?
    ): ServiceOptionTypes? {
        options ?: return null

        return ServiceOptionTypes(
            baggage = mapOptionItemEntity(options.baggage),
            cabin = mapOptionItemEntity(options.cabin),
            refundable = mapOptionItemEntity(options.refundable),
            exchangeable = mapOptionItemEntity(options.exchangeable),
            miles = mapOptionItemEntity(options.miles),
            seatsRegistration = mapOptionItemEntity(options.seatsRegistration),
            vipService = mapOptionItemEntity(options.vipService),
            meal = mapOptionItemEntity(options.meal)
        )
    }

    private fun mapOptionItemEntity(optionItemEntity: OptionItemEntity?): ServiceOptionInfo? {
        optionItemEntity ?: return null

        val serviceOptionInfoType: ServiceOptionInfoType
        val optionInfEntity: OptionInfoEntity

        if (!optionItemEntity.unknown.isNullOrEmpty()) {
            serviceOptionInfoType = ServiceOptionInfoType.UNKNOWN
            optionInfEntity = optionItemEntity.unknown.first()
        } else if (!optionItemEntity.free.isNullOrEmpty()) {
            serviceOptionInfoType = ServiceOptionInfoType.FREE
            optionInfEntity = optionItemEntity.free.first()
        } else if (!optionItemEntity.pay.isNullOrEmpty()) {
            serviceOptionInfoType = ServiceOptionInfoType.PAY
            optionInfEntity = optionItemEntity.pay.first()
        } else if (!optionItemEntity.notAvailable.isNullOrEmpty()) {
            serviceOptionInfoType = ServiceOptionInfoType.NOT_AVAILABLE
            optionInfEntity = optionItemEntity.notAvailable.first()
        } else {
            return null
        }

        return ServiceOptionInfo(
            serviceOptionInfoType = serviceOptionInfoType,
            count = optionInfEntity.count,
            description = optionInfEntity.description,
            measure = optionInfEntity.measure,
            weight = optionInfEntity.weight
        )
    }

    private fun mapAlternatives(alternativesEntity: List<AlternativeEntity>?): List<Alternative>? {
        alternativesEntity ?: return null

        return alternativesEntity
            .filterNot { it.coachType == TrainCoachType.OTHER }
            .map { alternativeEntity ->
                Alternative(
                    freeSeatsCount = alternativeEntity.freeSeatsCount,
                    price = alternativeEntity.price,
                    fee = alternativeEntity.fee,
                    currencyCode = alternativeEntity.currencyCode,
                    priceStatus = alternativeEntity.priceStatus,
                    coachType = alternativeEntity.coachType
                )
            }
    }

    private fun mapTrip(tripsEntity: TripsProp?): Trip? {
        tripsEntity ?: return null

        val tripEntityEntity: TripEntity? = when (tripsEntity.objectType) {
            ObjectTripType.BUS -> tripsEntity.tripBus
            ObjectTripType.FOOT -> tripsEntity.tripFoot
            ObjectTripType.TAXI -> tripsEntity.tripTaxi
            ObjectTripType.TRAIN -> tripsEntity.tripTrain
            ObjectTripType.TRAIN_SUBURBAN -> tripsEntity.tripTrainSuburban
            ObjectTripType.FLIGHT -> tripsEntity.tripFlight
            ObjectTripType.TRIP_BUS_TRANSFER -> tripsEntity.tripBusTransfer
            ObjectTripType.TRIP_TRAIN_SUBURBAN_TRANSFER -> tripsEntity.tripTrainSuburbanTransfer
            else -> null
        }

        tripEntityEntity ?: return null

        return Trip(
            objectType = tripsEntity.objectType,
            markCarrierCode = tripEntityEntity.markCarrierCode,
            markCarrierName = tripEntityEntity.markCarrierName,
            opCarrierCode = tripEntityEntity.opCarrierCode,
            carrierId = tripEntityEntity.carrierId,
            comment = tripEntityEntity.comment,
            status = tripEntityEntity.status,
            uniqueTripId = tripEntityEntity.uniqueTripId,
            id = tripEntityEntity.id,
            number = tripEntityEntity.number,
            opCarrierName = tripEntityEntity.opCarrierName,
            direction = tripEntityEntity.direction,
            fromId = tripEntityEntity.fromId,
            fromDescription = tripEntityEntity.fromDescr,
            toId = tripEntityEntity.toId,
            toDescription = tripEntityEntity.toDescr,
            transportClass = tripEntityEntity.transportClass,
            departure = tripEntityEntity.departure,
            arrival = tripEntityEntity.arrival,
            duration = tripEntityEntity.duration,
            carTypeCode = tripEntityEntity.carTypeCode,
            carTypeName = tripEntityEntity.carTypeName,
            carrierName = tripEntityEntity.carrierName,
            isReturnTrip = tripEntityEntity.isReturnTrip,
            description = tripEntityEntity.descr,
            tripType = tripEntityEntity.tripType,
            boardingDuration = tripEntityEntity.boardingDuration,
            distance = tripEntityEntity.distance,
            unboardingDuration = tripEntityEntity.unboardingDuration,
            isTransfer = tripEntityEntity.isTransfer,
            title = tripEntityEntity.title,
            hasTwoStoreyCoaches = tripEntityEntity.hasTwoStoreyCoaches,
            hasElectronicRegistration = tripEntityEntity.hasElectronicRegistration,
            options = mapOptions(tripEntityEntity.options)
        )
    }

    private fun mapOptions(optionsEntity: TripOptionsEntity?): TripOptions? {
        optionsEntity ?: return null

        return TripOptions(
            tv = optionsEntity.tv,
            bioToilet = optionsEntity.bioToilet,
            airConditioning = optionsEntity.airConditioning,
            baggage = optionsEntity.baggage,
            refundable = optionsEntity.refundable
        )
    }
}