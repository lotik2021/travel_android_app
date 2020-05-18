package ru.movista.data.source.local

const val OBJECT_ID = "object_id"
const val USER_RESPONSE = "user_response"

const val AGREEMENT_URL = "https://movista.ru/info/agreement_app"
const val PRIVACY_URL = "https://movista.ru/info/privacy"
const val PERSONAL_DATA_POLICY_URL = "https://movista.ru/info/personal_data_policy"
const val FILE_LOG_NAME = "movista_log.txt"

const val JSON_MEDIA_TYPE = "application/json; charset=utf-8"

const val GOOGLE_PLAY_APP_LINK = "https://play.google.com/store/apps/details?id=ru.movista"
const val TICKETS_PAY_CART_URL_PATTERN = "https://www.movista.ru/cart?uid=%s&%s"
const val DEFAULT_UTM_SOURCE = "utm_source=android"

object ObjectType {
    const val BOT_MESSAGE = "text_data"
    const val ROUTES_GOOGLE = "google_routes"
    const val TAXI = "trip_taxi"
    const val CAR = "trip_auto"
    const val ROUTES_MOVISTA = "movista_trip"
    const val PLACEHOLDER_MOVISTA = "movista_placeholder"
    const val AVAILABLE_TRAVEL_CARDS = "card"

    const val ROUTE_TAXI = "taxi_route"

    const val ROUTE_GOOGLE = "google_route_transport"
    const val ROUTE_MOVISTA = "movista_route"

    const val TRIP_ON_FOOT = "google_trip_foot"
    const val TRIP_BUS = "google_trip_bus"
    const val TRIP_SUBWAY = "google_trip_subway"
    const val TRIP_TRAM = "google_trip_tram"
    const val TRIP_COMMUTER_TRAIN = "google_trip_commutertrain" // пригородная эл-ка
    const val TRIP_HEAVY_RAIL = "google_trip_rail" // поезд
    const val TRIP_FERRY = "google_trip_ferry" // паром
    const val TRIP_TROLLEYBUS = "google_trip_trolleybus"
    const val TRIP_SHARE_TAXI = "google_trip_sharetaxi" // маршрутка
    //    const val TRIP_TAXI = "google_trip_taxi"
    const val TRIP_CAR = "google_route_auto"

    const val SKILL = "movista_skill"
    const val WEATHER = "weather"

    const val ADDRESS = "address"
}

object TravelCardType {
    const val TROIKA = 0
    const val STRELKA = 1
}

object ActionIds {
    const val CREATE_SESSION = "create_session"
    const val SEARCH_ROUTES = "return_route"
    const val BUILD_ROUTE = "build_route"
}

object HandableActionIds {
    const val RETRY = "-1"

    const val REFILL_TRAVEL_CARD = "card"
    const val CHOOSE_ON_MAP = "put_on_map"
    const val ENTER_ADDRESS = "enter_address"
    const val FROM_CURRENT_LOCATION = "from_current_location"
    const val UPDATE_APP = "update_required"

    const val DEPARTURE_TIME = "departure_time"
    const val ARRIVAL_TIME = "arrival_time"
    const val SPECIFY_DATE = "specify_date"
    const val CLEAR_CHAT = "clear_chat"
    const val SHOW_MAP = "show_map"
    const val FAVORITE_ADDRESS = "favorite_address"
    const val PLAN_MULTIMODAL_ROUTE = "plan_multimodal_route"
    const val BUY_TICKET_FLIGHT = "buy_ticket_flight"
    const val BUY_TICKET_TRAIN = "buy_ticket_train"
    const val BUY_TICKET_BUS = "buy_ticket_bus"

    fun isLongRouteAction(actionId: String) : Boolean {
        return actionId in arrayListOf(
            PLAN_MULTIMODAL_ROUTE,
            BUY_TICKET_FLIGHT,
            BUY_TICKET_TRAIN,
            BUY_TICKET_BUS
        )
    }
}

object ActionIdsThatNotRequiredLoading {
    const val CHOOSE_ON_MAP = "put_on_map"
    const val ENTER_ADDRESS = "enter_address"
    const val UPDATE_APP = "update_required"
    const val DEPARTURE_TIME = HandableActionIds.DEPARTURE_TIME
    const val ARRIVAL_TIME = HandableActionIds.ARRIVAL_TIME
    const val SPECIFY_DATE = HandableActionIds.SPECIFY_DATE
    const val SHOW_MAP = HandableActionIds.SHOW_MAP
    const val FAVORITE_ADDRESS = HandableActionIds.FAVORITE_ADDRESS
    const val PLAN_MULTIMODAL_ROUTE = HandableActionIds.PLAN_MULTIMODAL_ROUTE
    const val BUY_TICKET_FLIGHT = HandableActionIds.BUY_TICKET_FLIGHT
    const val BUY_TICKET_TRAIN = HandableActionIds.BUY_TICKET_TRAIN
    const val BUY_TICKET_BUS = HandableActionIds.BUY_TICKET_BUS
}

object MovistaTripTypes {
    const val FLIGHT = "flight"
    const val BUS = "bus"
    const val TRAIN = "train"
}

enum class SocialNetwork(val url: String, val analyticTitle: String) {
    VK("https://vk.com/movista", "Вконтакте"),
    INSTAGRAM("https://www.instagram.com/movista_ru", "Instagram"),
    OK("https://ok.ru/group/59009242693690", "Одноклассники"),
    FACEBOOK("https://www.facebook.com/movista.ru", "Facebook"),
    YOUTUBE("https://www.youtube.com/channel/UCe-aFgHtnL1A48c13e5LmEw", "Youtube"),
    TWITTER("https://twitter.com/Movista_ru", "Twitter")
}