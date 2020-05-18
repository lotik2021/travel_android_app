package ru.movista.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import ru.movista.data.source.local.models.RouteFilterType
import ru.movista.domain.model.ProfileTransportType
import ru.movista.domain.model.UserFavoritesPlaces
import ru.movista.domain.model.tickets.PathGroup
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.model.tickets.TripGroup
import ru.movista.presentation.about.AboutAppFragment
import ru.movista.presentation.agencies.AgenciesFragment
import ru.movista.presentation.auth.AuthFlowFragment
import ru.movista.presentation.auth.enterphone.EnterPhoneFragment
import ru.movista.presentation.auth.registration.RegistrationFragment
import ru.movista.presentation.auth.verifyphone.VerifyPhoneFragment
import ru.movista.presentation.chat.ChatFragment
import ru.movista.presentation.documents.LegalDocumentsFragment
import ru.movista.presentation.intro.IntroFragment
import ru.movista.presentation.placesearch.SearchPlaceFragment
import ru.movista.presentation.placesearch.UserHistoryTab
import ru.movista.presentation.profile.ProfileFragment
import ru.movista.presentation.profile.edit.EditProfileFragment
import ru.movista.presentation.profile.favorites.FavoritesFragment
import ru.movista.presentation.profile.transporttypes.TransportTypesFragment
import ru.movista.presentation.refilltravelcard.StrelkaViewModel
import ru.movista.presentation.refilltravelcard.TroikaViewModel
import ru.movista.presentation.refilltravelcard.refillstrelka.RefillStrelkaFragment
import ru.movista.presentation.refilltravelcard.refilltroika.RefillTroikaFragment
import ru.movista.presentation.routedetail.RouteDetailFragment
import ru.movista.presentation.selectplaceonmap.SelectPlaceOnMapFragment
import ru.movista.presentation.tickets.basket.BasketFragment
import ru.movista.presentation.tickets.dateselect.DateSelectFragment
import ru.movista.presentation.tickets.detailroute.DetailRoutesFragment
import ru.movista.presentation.tickets.detailroute.DetailRoutesMode
import ru.movista.presentation.tickets.passengers.PassengersFragment
import ru.movista.presentation.tickets.placeselect.PlaceSelectFragment
import ru.movista.presentation.tickets.searchtickets.SearchTicketsFragment
import ru.movista.presentation.tickets.segments.RoutePlaceFilter
import ru.movista.presentation.tickets.segments.SegmentArgumentsWrapper
import ru.movista.presentation.tickets.segments.SegmentsFragment
import ru.movista.presentation.tickets.segments.UserRouteFilter
import ru.movista.presentation.tickets.segments.providersfilter.RoutePlaceFilterFragment
import ru.movista.presentation.tickets.segments.segmentfilter.SegmentFilterFragment
import ru.movista.presentation.tickets.tripgroup.TripGroupFragment
import ru.movista.presentation.viewmodel.AgencyViewModel
import ru.movista.presentation.viewmodel.FavoriteModeViewModel
import ru.movista.presentation.viewmodel.RouteDetailsViewModel
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    class Main : SupportAppScreen() {
        companion object {
            const val TAG = "main"
        }
    }

    class Chat : SupportAppScreen() {
        companion object {
            const val TAG = "chat"
        }

        override fun getFragment(): Fragment {
            return ChatFragment()
        }
    }

    class SelectPlaceOnMap(private val isSimpleMap: Boolean = false) : SupportAppScreen() {
        companion object {
            const val TAG = "select_place_on_map"
        }

        override fun getFragment(): Fragment {
            return SelectPlaceOnMapFragment.newInstance(isSimpleMap)
        }
    }

    class PlaceSearch(
        private val favoriteModeViewModel: FavoriteModeViewModel? = null,
        private val userHistoryTab: UserHistoryTab = UserHistoryTab.RECENT
    ) : SupportAppScreen() {
        companion object {
            const val TAG = "place_search"
        }

        override fun getFragment(): Fragment {
            return SearchPlaceFragment.newInstance(favoriteModeViewModel, userHistoryTab)
        }
    }

    class RefillTroika(private val troikaViewModel: TroikaViewModel) : SupportAppScreen() {
        companion object {
            const val TAG = "refill_troika"
        }

        override fun getFragment(): Fragment {
            return RefillTroikaFragment.newInstance(troikaViewModel)
        }
    }

    class RefillStrelka(private val strelkaViewModel: StrelkaViewModel) : SupportAppScreen() {
        companion object {
            const val TAG = "refill_strelka"
        }

        override fun getFragment(): Fragment {
            return RefillStrelkaFragment.newInstance(strelkaViewModel)
        }
    }

    class RouteDetail(private val route: RouteDetailsViewModel) : SupportAppScreen() {
        companion object {
            const val TAG = "route_detail"
        }

        override fun getFragment(): Fragment {
            return RouteDetailFragment.newInstance(route)
        }
    }

    class Agencies(private val agencies: List<AgencyViewModel>) : SupportAppScreen() {
        companion object {
            const val TAG = "agencies"
        }

        override fun getFragment(): Fragment {
            return AgenciesFragment.newInstance(agencies)
        }
    }

    class ExternalAppByUrl(private val url: String) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent {
            return Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
        }
    }

    class Intro : SupportAppScreen() {
        companion object {
            const val TAG = "intro"
        }

        override fun getFragment(): Fragment {
            return IntroFragment.newInstance()
        }
    }

    class AboutApp : SupportAppScreen() {
        companion object {
            const val TAG = "about_app"
        }

        override fun getFragment(): Fragment {
            return AboutAppFragment.newInstance()
        }
    }

    class RefillTravelCardDialog {
        companion object {
            const val TAG = "refill_travel_card_dialog"
        }
    }

    class ChooseAppDialog {
        companion object {
            const val TAG = "choose_app"
        }
    }

    class TaxiOrderDialog {
        companion object {
            const val TAG = "taxi_order"
        }
    }

    class AuthFlow : SupportAppScreen() {
        companion object {
            const val TAG = "auth_flow"
        }

        override fun getFragment(): Fragment {
            return AuthFlowFragment.newInstance()
        }
    }

    class EnterPhone : SupportAppScreen() {
        companion object {
            const val TAG = "enter_phone"

        }

        override fun getFragment(): Fragment {
            return EnterPhoneFragment.newInstance()
        }

    }

    class VerifyPhone : SupportAppScreen() {
        companion object {
            const val TAG = "verify_phone"

        }

        override fun getFragment(): Fragment {
            return VerifyPhoneFragment.newInstance()
        }

    }

    class Registration : SupportAppScreen() {
        companion object {
            const val TAG = "registration"
        }

        override fun getFragment(): Fragment {
            return RegistrationFragment.newInstance()
        }
    }

    class Profile : SupportAppScreen() {
        companion object {
            const val TAG = "profile"
        }

        override fun getFragment(): Fragment {
            return ProfileFragment.newInstance()
        }
    }

    class LegalDocuments : SupportAppScreen() {
        companion object {
            const val TAG = "legal_documents"
        }

        override fun getFragment(): Fragment {
            return LegalDocumentsFragment.newInstance()
        }
    }

    class EditProfile : SupportAppScreen() {
        companion object {
            const val TAG = "edit_profile"
        }

        override fun getFragment(): Fragment {
            return EditProfileFragment.newInstance()
        }
    }

    class TransportTypes(private val profileTransportTypes: List<ProfileTransportType>) : SupportAppScreen() {
        companion object {
            const val TAG = "transport_types"
        }

        override fun getFragment(): Fragment {
            return TransportTypesFragment.newInstance(profileTransportTypes)
        }
    }

    class Favorites(private val userFavoritesPlaces: UserFavoritesPlaces) : SupportAppScreen() {
        companion object {
            const val TAG = "favorites"
        }

        override fun getFragment(): Fragment {
            return FavoritesFragment.newInstance(userFavoritesPlaces)
        }
    }

    class Map : SupportAppScreen() {
        companion object {
            const val TAG = "map"
        }
    }

    // --- Tickets ---
    class SearchTickets : SupportAppScreen() {
        companion object {
            const val TAG = "search_tickets"
        }

        override fun getFragment(): Fragment {
            return SearchTicketsFragment.newInstance()
        }
    }

    class DateSelect : SupportAppScreen() {
        companion object {
            const val TAG = "date_select"
        }

        override fun getFragment(): Fragment {
            return DateSelectFragment.newInstance()
        }
    }

    class Passengers : SupportAppScreen() {
        companion object {
            const val TAG = "passengers"
        }

        override fun getFragment(): Fragment {
            return PassengersFragment.newInstance()
        }
    }

    class PlaceSelect(private val type: String) : SupportAppScreen() {
        companion object {
            const val TAG = "city_select"
        }

        override fun getFragment(): Fragment {
            return PlaceSelectFragment.newInstance(type)
        }
    }

    class SegmentFilter(
        private val routes: List<Route>,
        private val userRouteFilter: UserRouteFilter? = null,
        private val searchParams: SearchModel
    ) : SupportAppScreen() {
        companion object {
            const val TAG = "segment_filter"
        }

        override fun getFragment(): Fragment {
            return SegmentFilterFragment.newInstance(routes, userRouteFilter, searchParams)
        }
    }

    class SelectableDataFilter(
        private val filterType: RouteFilterType,
        private val routePlaceFilters: List<RoutePlaceFilter>,
        private val searchParams: SearchModel
    ) : SupportAppScreen() {
        companion object {
            const val TAG = "selectable_data_filter"
        }

        override fun getFragment(): Fragment {
            return RoutePlaceFilterFragment.newInstance(filterType, routePlaceFilters, searchParams)
        }
    }

    class TripGroupScreen(private val searchParams: SearchModel) : SupportAppScreen() {
        companion object {
            const val TAG = "trip_group"
        }

        override fun getFragment(): Fragment {
            return TripGroupFragment.newInstance(searchParams)
        }
    }

    class Basket(
        private val searchParams: SearchModel? = null, // null для того, чтобы можно было вызвать backTo
        private val searchUid: String? = null,
        private val tripGroup: TripGroup? = null,
        private val pathGroup: PathGroup? = null
    ) : SupportAppScreen() {
        companion object {
            const val TAG = "basket"
        }

        override fun getFragment(): Fragment {
            return BasketFragment.newInstance(searchParams, searchUid, tripGroup, pathGroup)
        }
    }

    class Segments(private val segmentArguments: SegmentArgumentsWrapper) : SupportAppScreen() {
        companion object {
            const val TAG = "segments"
        }

        override fun getFragment(): Fragment {
            return SegmentsFragment.newInstance(segmentArguments)
        }
    }

    class DetailRoutes(
        private val segmentArguments: SegmentArgumentsWrapper,
        private val detailRoutesMode: DetailRoutesMode
    ) : SupportAppScreen() {
        companion object {
            const val TAG = "details_route"
        }

        override fun getFragment(): Fragment {
            return DetailRoutesFragment.newInstance(segmentArguments, detailRoutesMode)
        }
    }
}