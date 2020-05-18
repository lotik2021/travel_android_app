package ru.movista.analytics

import android.app.Activity
import androidx.fragment.app.Fragment
import ru.movista.analytics.performers.AppsFlyerAnalyticEventPerformer
import ru.movista.analytics.performers.FirabaseAnalyticEventPerformer
import ru.movista.analytics.performers.LocalAnalyticEventPerformer
import ru.movista.analytics.performers.YandexAnalyticEventPerformer
import ru.movista.data.source.local.DeviceInfo
import ru.movista.presentation.about.AboutAppFragment
import ru.movista.presentation.agencies.AgenciesFragment
import ru.movista.presentation.auth.enterphone.EnterPhoneFragment
import ru.movista.presentation.auth.registration.RegistrationFragment
import ru.movista.presentation.auth.verifyphone.VerifyPhoneFragment
import ru.movista.presentation.carnavigation.CarNavigationDialogFragment
import ru.movista.presentation.chat.ChatFragment
import ru.movista.presentation.documents.LegalDocumentsFragment
import ru.movista.presentation.intro.IntroFragment
import ru.movista.presentation.placesearch.SearchPlaceFragment
import ru.movista.presentation.profile.ProfileFragment
import ru.movista.presentation.profile.favorites.FavoritesFragment
import ru.movista.presentation.profile.transporttypes.TransportTypesFragment
import ru.movista.presentation.refilltravelcard.RefillTravelCardDialogFragment
import ru.movista.presentation.refilltravelcard.refillstrelka.RefillStrelkaFragment
import ru.movista.presentation.refilltravelcard.refilltroika.RefillTroikaFragment
import ru.movista.presentation.routedetail.RouteDetailFragment
import ru.movista.presentation.selectplaceonmap.SelectPlaceOnMapFragment
import ru.movista.presentation.taxiorder.TaxiOrderDialogFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    yandexAnalyticEventPerformer: YandexAnalyticEventPerformer,
    appsFlyerAnalyticEventPerformer: AppsFlyerAnalyticEventPerformer,
    private val firabaseAnalyticEventPerformer: FirabaseAnalyticEventPerformer,
    deviceInfo: DeviceInfo
) {

    private val deviceId = deviceInfo.deviceId

    private val analyticEventPerformers = arrayOf(
        LocalAnalyticEventPerformer,
        yandexAnalyticEventPerformer,
        appsFlyerAnalyticEventPerformer,
        firabaseAnalyticEventPerformer
    )

    fun reportChangeCurrentScreenFromFragment(activity: Activity?, screen: Fragment) {
        activity ?: return

        val name = when (screen) {
            is ChatFragment -> "main_messaging_screen"
            is IntroFragment -> "onboarding_screen"
            is AboutAppFragment -> "about_screen"
            is RefillTroikaFragment -> "travel_card_screen"
            is RefillStrelkaFragment -> "travel_card_screen"
            is SelectPlaceOnMapFragment -> "search_on_map_screen"
            is SearchPlaceFragment -> "search_address_screen"
            is RouteDetailFragment -> "google_route_details_screen"
            is RefillTravelCardDialogFragment -> "travel_card_choice_screen"
            is AgenciesFragment -> "suppliers_info_screen"
            is ProfileFragment -> "profile_screen"
            is EnterPhoneFragment -> "phone_entrance_screen"
            is VerifyPhoneFragment -> "code_entrance_screen"
            is RegistrationFragment -> "registration_screen"
            is LegalDocumentsFragment -> "legal_documents_screen"
            is TransportTypesFragment -> "transport_settings_screen"
            is CarNavigationDialogFragment -> "auto_details_screen"
            is TaxiOrderDialogFragment -> "taxi_details_screen"
            is FavoritesFragment -> "favorites_screen"
            else -> return
        }

        firabaseAnalyticEventPerformer.firebaseAnalytics.setCurrentScreen(
            activity,
            name,
            screen.javaClass.canonicalName
        )
    }

    fun reportChangeCurrentScreenName(activity: Activity?, name: String) {
        activity ?: return
        firabaseAnalyticEventPerformer.firebaseAnalytics.setCurrentScreen(activity, name, name)
    }

    fun reportOnboardingAllowedLocation() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "onboarding_allowed_location",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportOnboardingDisallowedLocation() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "onboarding_disallowed_location",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportLegalAccepted() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "onboarding_allowed_legal",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportOnboardingFinished() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "onboarding_finished",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportUserSendMessage(message: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "chat_send_message",
                    mapOf(
                        "message" to message,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportSelectAction(id: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "chat_select_action",
                    mapOf(
                        "action_id" to id,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportSelectCarRoute(routeId: String, position: Int) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "chat_select_route_auto",
                    mapOf(
                        "id" to routeId,
                        "position" to position,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportSelectTaxiRoute(routeId: String, position: Int) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "chat_select_route_taxi",
                    mapOf(
                        "id" to routeId,
                        "position" to position,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportSelectRouteTransport(routeId: String, position: Int) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "chat_select_route_transport",
                    mapOf(
                        "id" to routeId,
                        "position" to position,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportSelectRouteMovista(routeId: String, position: Int) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "chat_select_route_movista",
                    mapOf(
                        "id" to routeId,
                        "position" to position,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportSelectMovistaPlaceholder(routeId: String, count: Int) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "chat_select_movista_placeholder",
                    mapOf(
                        "id" to routeId,
                        "count" to count,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportTravelCardChoice(travelCardId: Int, travelCardName: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "travel_card_choice",
                    mapOf(
                        "travel_card_id" to travelCardId,
                        "travel_card_name" to travelCardName,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportTravelCardPay(travelCardId: String, travelCardName: String, amount: Int) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "travel_card_pay",
                    mapOf(
                        "travel_card_id" to travelCardId,
                        "travel_card_name" to travelCardName,
                        "amount" to amount,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportTravelCardRequestBalance(travelCardId: Int, travelCardName: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "travel_card_request_balance",
                    mapOf(
                        "travel_card_id" to travelCardId,
                        "travel_card_name" to travelCardName,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportTaxiDetailsOrder(taxiName: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "taxi_details_order",
                    mapOf(
                        "taxi_name" to taxiName,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportStartCarNavigation(navigatorName: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "auto_details_start_navigation",
                    mapOf(
                        "navigator_name" to navigatorName,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportRouteDetailsSelectTaxiTrip(taxiName: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "route_details_select_trip_taxi",
                    mapOf(
                        "taxi_name" to taxiName,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportOpenProfile() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "open_profile",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportLoginStart() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "login_start",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportLoginEnterPhone() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "login_enter_phone",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportLoginSuccess() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "login_code_verified",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportRegistrationStart() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "sign_up_open",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportRegistrationSuccess() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "sign_up_succes",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportOpenFavorites() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "open_favorites",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportAddFavoriteClick() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "add_favorite_click",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportHomeFavoriteEdit() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "home_favorite_edit",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportWorkFavoriteEdit() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "work_favorite_edit",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportUpdatedHomeAddress(address: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "updated_home_address",
                    mapOf(
                        "address" to address,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportUpdatedWorkAddress(address: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "updated_work_address",
                    mapOf(
                        "address" to address,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportAddedFavoriteAddress(address: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "added_favorite_address",
                    mapOf(
                        "address" to address,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportDeletedFavorite(address: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "deleted_favorite",
                    mapOf(
                        "address" to address,
                        "device_id" to deviceId
                    )
                )
            )
        }
    }

    fun reportDeletedFavoriteClick() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "delete_favorite_click",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportOpenSearchRoutes() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "open_search_routes",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportOpenChat() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "open_chat",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportSearchStartRoutes(
        fromId: Long,
        toId: Long,
        owert: String,
        departureDate: String,
        returnDepartureDate: String?,
        passengerNum: Int
    ) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "search_start_routes",
                    mapOf(
                        "device_id" to deviceId,
                        "from_id" to fromId,
                        "to_id" to toId,
                        "owert" to owert,
                        "departure_date" to departureDate,
                        "return_departure_date" to returnDepartureDate.toString(),
                        "passenger_num" to passengerNum
                    )
                )
            )
        }
    }

    fun reportGroupOfRoutesClick(
        groupId: String,
        groupsCount: Int,
        priceSelected: Double,
        segmentsCount: Int
    ) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "group_of_routes_click",
                    mapOf(
                        "device_id" to deviceId,
                        "group_id" to groupId,
                        "groups_count" to groupsCount,
                        "price_selected" to priceSelected,
                        "segments_count" to segmentsCount
                    )
                )
            )
        }
    }

    fun reportRouteSegmentClick(
        tripType: String,
        departureDate: String
    ) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "route_segment_click",
                    mapOf(
                        "device_id" to deviceId,
                        "trip_type" to tripType,
                        "departure_date" to departureDate
                    )
                )
            )
        }
    }

    fun reportBackwardsClick(screenName: String) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "backwards_click",
                    mapOf(
                        "device_id" to deviceId,
                        "screen_name" to screenName
                    )
                )
            )
        }
    }

    fun reportOpenTicketDetails(
        tripType: String,
        carrier: String,
        totalDuration: Long,
        price: Double,
        transferCount: Int,
        position: Int,
        isFiltered: Boolean
    ) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "open_ticket_details",
                    mapOf(
                        "device_id" to deviceId,
                        "trip_type" to tripType,
                        "carrier" to carrier,
                        "total_duration" to totalDuration,
                        "price" to price,
                        "transfer_count" to transferCount,
                        "position" to position,
                        "is_filtered" to isFiltered
                    )
                )
            )
        }
    }

    fun reportChooseTickets(
        tripType: String,
        price: Double
    ) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "choose_tickets",
                    mapOf(
                        "device_id" to deviceId,
                        "trip_type" to tripType,
                        "price" to price
                    )
                )
            )
        }
    }

    fun reportBuyRoute(
        groupId: String,
        price: Double,
        passengerNum: Int,
        segmentsCount: Int,
        link: String,
        totalDuration: Int
    ) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "buy_route",
                    mapOf(
                        "device_id" to deviceId,
                        "group_id" to groupId,
                        "price" to price,
                        "passenger_num" to passengerNum,
                        "segments_count" to segmentsCount,
                        "link" to link,
                        "total_duration" to totalDuration
                    )
                )
            )
        }
    }

    fun reportChangeRoute() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "change_route",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportOpenFilter() {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "open_filter",
                    mapOf("device_id" to deviceId)
                )
            )
        }
    }

    fun reportSetFilter(isFiltered: Boolean) {
        analyticEventPerformers.forEach {
            it.perform(
                ActionWithDataAnalyticEvent(
                    "set_filter",
                    mapOf(
                        "device_id" to deviceId,
                        "is_filtered" to isFiltered
                    )
                )
            )
        }
    }
}