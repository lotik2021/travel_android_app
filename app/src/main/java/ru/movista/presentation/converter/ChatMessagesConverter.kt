package ru.movista.presentation.converter

import android.content.res.Resources
import android.text.Html
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import ru.movista.R
import ru.movista.data.entity.*
import ru.movista.data.entity.objects.*
import ru.movista.data.source.local.MovistaTripTypes
import ru.movista.di.FragmentScope
import ru.movista.presentation.chat.*
import ru.movista.presentation.viewmodel.*
import ru.movista.utils.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@FragmentScope
class ChatMessagesConverter @Inject constructor(
    private val resources: Resources,
    private val placeSearchResultConverter: PlaceSearchResultConverter
) {

    fun objectsToChatItems(objects: List<ObjectEntity>?): List<ChatItem> {
        val chatItems = arrayListOf<ChatItem>()

        if (objects == null) {
            reportNullFieldError("response.objects")
            return chatItems
        }

        var skillsItem: SkillsItem? = null

        objects.forEach { objectEntity ->
            when (objectEntity) {
                is BotMessageEntity -> {
                    chatItems.add(BotMessage(objectEntity.data.text))
                }
                is ShortRoutesEntity -> {
                    chatItems.add(RoutesItem(routesToRoutesViewModel(objectEntity.data.routes)))
                }
                is TaxiEntity -> {
                    chatItems.add(TaxiItem(toTaxiProvidersViewModel(objectEntity.data.routes)))
                }
                is CarEntity -> {
                    chatItems.add(CarItem(toGoogleCarViewModel(objectEntity.data.routes)))
                }
                is LongRoutesEntity -> {
                    chatItems.add(LongRoutesItem(toLongRoutesViewModel(objectEntity.data.routes)))
                }
                is MovistaPlaceholderEntity -> {
                    chatItems.add(MovistaPlaceholderItem(toMovistaPlaceholderItem(objectEntity.data)))
                }
                is SkillEntity -> {
                    if (skillsItem == null) {
                        skillsItem = SkillsItem(mutableListOf<SkillViewModel>())
                        chatItems.add(skillsItem as SkillsItem)
                    }
                    (skillsItem as SkillsItem).skills.add(toSkillItem(objectEntity.data))
                }
                is AddressesEntity -> {
                    chatItems.add(
                        AddressesItem(
                            placeSearchResultConverter.toChatAddressesViewModel(objectEntity.data),
                            objectEntity.data.token
                        )
                    )
                }
                is WeatherEntity -> {
                    chatItems.add(
                        WeatherItem(toWeatherViewModelViewModel(objectEntity.data))
                    )
                }
            }
        }
        return chatItems
    }


    private fun toWeatherViewModelViewModel(entity: WeatherDataEntity): WeatherViewModel {
        return WeatherViewModel(
            temperature = entity.main.temp.roundToInt(),
            description = entity.weather.firstOrNull()?.description ?: "",
            pressure = entity.main.pressure,
            humidity = entity.main.humidity,
            windSpeed = entity.wind.speed,
            iconUrl = entity.android_icon_url.firstOrNull(),
            iosIconUrl = entity.ios_icon_url.firstOrNull()
        )
    }

    private fun routesToRoutesViewModel(shortRouteEntities: List<ShortRouteEntity>): List<RouteViewModel> {
        val routeViewModels = arrayListOf<RouteViewModel>()
        shortRouteEntities.forEach {
            if (it is GoogleRouteEntity) {
                val startTime = dateTimeToHHMM(it.data.start_time)
                val endTime = dateTimeToHHMM(it.data.end_time)

                val trips = tripsToTripsViewModel(it.data.trips)

                // был вылет тк трипов в роуте не было
                if (trips.isNotEmpty()) {
                    routeViewModels.add(
                        RouteViewModel(
                            id = it.id,
                            departureName = it.data.from_address,
                            destinationName = it.data.to_address,
                            endTime = endTime,
                            durationMinutes = it.data.duration.toString(),
                            durationWithHours = minutesToDuration(it.data.duration),
                            startToEndTime = "$startTime - $endTime",
                            trips = trips,
                            agencies = toAgenciesViewModel(it.data.agencies)
                        )
                    )
                }
            }
        }
        return routeViewModels
    }

    private fun toAgenciesViewModel(agencies: List<AgencyEntity>?): List<AgencyViewModel> {
        val result = arrayListOf<AgencyViewModel>()
        agencies?.forEach {
            result.add(AgencyViewModel(it.name, it.phone, it.url))
        }
        return result
    }

    private fun tripsToTripsViewModel(tripsEntities: List<TripEntity>): List<TripViewModel> {
        val tripViewModels = arrayListOf<TripViewModel>()
        tripsEntities.forEach { tripEntity ->
            when (tripEntity) {
                is OnFootTripEntity -> {
                    tripViewModels.add(
                        OnFootViewModel(
                            distance = "${tripEntity.data.distance} м",
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time)
                        )
                    )
                }
                is BusTripEntity -> {
                    tripViewModels.add(
                        BusViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            stopsCount = resources.getQuantityString(
                                R.plurals.stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            lineName = "",
                            lineNumber = tripEntity.data.line_number,
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is TramTripEntity -> {
                    tripViewModels.add(
                        TramViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            stopsCount = resources.getQuantityString(
                                R.plurals.stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            lineName = "",
                            lineNumber = tripEntity.data.line_number,
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is HeavyRailTripEntity -> {
                    tripViewModels.add(
                        HeavyRailViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            stopsCount = resources.getQuantityString(
                                R.plurals.stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            lineName = tripEntity.data.line_name,
                            lineNumber = "",
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is FerryTripEntity -> {
                    tripViewModels.add(
                        FerryViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            stopsCount = resources.getQuantityString(
                                R.plurals.stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            lineName = tripEntity.data.line_name,
                            lineNumber = "",
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is TrolleybusTripEntity -> {
                    tripViewModels.add(
                        TrolleybusViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            stopsCount = resources.getQuantityString(
                                R.plurals.stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            lineName = "",
                            lineNumber = tripEntity.data.line_number,
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is ShareTaxiTripEntity -> {
                    tripViewModels.add(
                        ShareTaxiViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            stopsCount = resources.getQuantityString(
                                R.plurals.stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            lineName = "",
                            lineNumber = tripEntity.data.line_number,
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is SubwayTripEntity -> {
                    tripViewModels.add(
                        SubwayViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            bgColor = tripEntity.data.line_color,
                            lineName = tripEntity.data.line_name,
                            lineNumber = tripEntity.data.line_number,
                            stationsCount = resources.getQuantityString(
                                R.plurals.stations_stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is CommuterTrainTripEntity -> {
                    tripViewModels.add(
                        CommuterTrainViewModel(
                            duration = secondsToDuration(tripEntity.data.duration),
                            polyline = tripEntity.data.polyline,
                            lineName = resources.getString(
                                R.string.commuter_train_format,
                                tripEntity.data.trip_short_name,
                                tripEntity.data.line_name
                            ),
                            startTime = dateTimeToHHMM(tripEntity.data.departure_time),
                            endTime = dateTimeToHHMM(tripEntity.data.arrival_time),
                            stopsCount = resources.getQuantityString(
                                R.plurals.stops_count,
                                tripEntity.data.number_stops,
                                tripEntity.data.number_stops
                            ),
                            departureName = tripEntity.data.departure_stop,
                            destinationName = tripEntity.data.arrival_stop,
                            lineNumber = "",
                            tripShortName = tripEntity.data.trip_short_name,
                            icon = tripEntity.data.generic_data?.android_icon_short,
                            detailIcon = tripEntity.data.generic_data?.android_icon_details,
                            iosIcon = tripEntity.data.generic_data?.icon_short,
                            iosDetailIcon = tripEntity.data.generic_data?.icon_details
                        )
                    )
                }
                is TaxiTripEntity -> {
                    // данные для трипа берем из первого провайдера
                    val firstProvider = tripEntity.data.routes[0] as? CommonTaxiRouteEntity
                    firstProvider?.data?.let {
                        tripViewModels.add(
                            TaxiViewModel(
                                duration = secondsToDuration(it.duration),
                                polyline = it.polyline,
                                startTime = dateTimeToHHMM(it.start_time),
                                endTime = dateTimeToHHMM(it.end_time),
                                fromAddress = it.from_address,
                                toAddress = it.to_address,
                                providers = toTaxiProvidersViewModel(tripEntity.data.routes),
                                icon = it.android_icon_url,
                                iosIcon = it.ios_icon_url
                            )
                        )
                    }

                }
            }
        }
        return tripViewModels
    }

    private fun toTaxiProvidersViewModel(taxisEntity: List<TaxiRouteEntity>): List<TaxiProviderViewModel> {
        val taxiProvidersViewModel = arrayListOf<TaxiProviderViewModel>()

        taxisEntity.forEach {
            when (it) {
                is CommonTaxiRouteEntity -> {
                    val startTime = dateTimeToHHMM(it.data.start_time)
                    val endTime = dateTimeToHHMM(it.data.end_time)
                    val duration = secondsToDuration(it.data.duration)
                    taxiProvidersViewModel.add(
                        TaxiProviderViewModel(
                            taxiProviderId = it.id,
                            taxiProviderStartTime = startTime,
                            taxiProviderEndTime = endTime,
                            taxiProviderDuration = duration,
                            taxiProviderMinPrice = it.data.fares[0].price,
                            taxiProviderDescription = it.data.provider_description,
                            taxiProviderLink = it.data.deeplinks.android,
                            taxiProviderStoreLink = it.data.deeplinks.android_store,
                            taxiProviderDeeplinkId = it.data.deeplinks.guid,
                            taxiProviderTariffs = convertTaxiTariffs(it.data.fares),
                            taxiProviderImage = it.data.android_icon_url,
                            taxiProviderIosImage = it.data.ios_icon_url,
                            taxiProviderPolyline = it.data.polyline
                        )
                    )
                }
            }
        }
        return taxiProvidersViewModel
    }

    private fun convertTaxiTariffs(commonTaxiFaresDataEntity: List<CommonTaxiFaresDataEntity>): List<TaxiTariff> {
        val tariffs = arrayListOf<TaxiTariff>()
        commonTaxiFaresDataEntity.forEach {
            tariffs.add(TaxiTariff(it.name, it.price))
        }
        return tariffs
    }

    private fun toGoogleCarViewModel(carRouteEntity: List<CarRouteEntity>): List<CarRouteViewModel> {
        val result = arrayListOf<CarRouteViewModel>()

        carRouteEntity.forEach {
            if (it is GoogleCarEntity) {
                result.add(
                    GoogleCarViewModel(
                        id = it.id,
                        minDuration = secondsToDuration(it.data.duration),
                        maxDuration = secondsToDuration(it.data.duration_in_traffic),
                        distance = metersToKm(it.data.distance),
                        startTime = dateTimeToHHMM(it.data.start_time),
                        minEndTime = dateTimePlusSeconds(it.data.start_time, it.data.duration),
                        maxEndTime = dateTimePlusSeconds(
                            it.data.start_time,
                            it.data.duration_in_traffic
                        ),
                        deeplinks = toCarDeeplinkViewModel(it.data.deeplinks),
                        routeThrough = it.data.summary,
                        polyline = it.data.polyline
                    )
                )
            }
        }

        return result
    }

    private fun toCarDeeplinkViewModel(deeplinks: List<DeeplinkDataEntity>): List<CarDeeplinkViewModel> {
        val result = arrayListOf<CarDeeplinkViewModel>()
        deeplinks.forEach {

            if (it.android.isNotEmpty()) result.add(CarDeeplinkViewModel(it.title, it.android, it.icon_name))
        }
        return result
    }

    private fun toLongRoutesViewModel(longRoutes: List<LongRouteEntity>): List<MovistaRouteViewModel> {
        val result = arrayListOf<MovistaRouteViewModel>()
        longRoutes.forEach {
            if (it is MovistaRouteEntity) {
                result.add(
                    with(it) {
                        MovistaRouteViewModel(
                            id = id,
                            startDate = dateTimeToDate(data.departure_time),
                            endDate = dateTimeToDate(data.arrival_time),
                            startTime = dateTimeToHHMM(data.departure_time),
                            endTime = dateTimeToHHMM(data.arrival_time),
                            duration = secondsToDuration(data.duration),
                            changesCount = resources.getQuantityString(
                                R.plurals.changes_count,
                                data.number_of_transfers,
                                data.number_of_transfers
                            ),
                            price = resources.getString(R.string.price, data.price.toInt()),
                            redirectUrl = data.redirect_url,
                            trips = toLongRouteTripTypes(data.trips)
                        )
                    }
                )
            }
        }
        return result
    }

    private fun toLongRouteTripTypes(strings: List<String>): List<LongRouteTripType> {
        val result = arrayListOf<LongRouteTripType>()
        strings.forEach {
            when (it) {
                MovistaTripTypes.FLIGHT -> result.add(FlightLongRouteTripType())
                MovistaTripTypes.BUS -> result.add(BusLongRouteTripType())
                MovistaTripTypes.TRAIN -> result.add(TrainLongRouteTripType())
            }
        }
        return result
    }

    private fun toMovistaPlaceholderItem(data: MovistaPlaceholderDataEntity): MovistaPlaceholderViewModel {
        val textString =
            resources.getQuantityString(R.plurals.movista_route_options_count, data.total_count, data.total_count)
        val formattedText = Html.fromHtml(textString)
        return MovistaPlaceholderViewModel(
            data.id,
            data.total_count,
            formattedText,
            data.redirect_url
        )
    }

    private fun toSkillItem(entity: SkillDataEntity): SkillViewModel {
        return SkillViewModel(
            entity.action_id,
            entity.description,
            entity.android_icon_url,
            entity.ios_icon_url
        )
    }

    private fun metersToKm(meters: Int): String {
        return "${meters / 1000} км"
    }

    private fun dateTimePlusSeconds(dateTime: String, seconds: Int): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(dateTime)
            val plusSeconds = zonedDateTime.plusSeconds(seconds.toLong())
            plusSeconds.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            Timber.e(e)
            String.EMPTY
        }
    }
}