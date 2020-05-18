package ru.movista.di

import ru.movista.di.component.*
import ru.movista.di.component.tickets.*
import ru.movista.presentation.Screens

object Injector {

    private lateinit var appComponent: AppComponent

    var mainComponent: MainComponent? = null
        get() {
            if (field == null) {
                initMainComponent()
            }
            return field
        }

    var chatComponent: ChatComponent? = null

    var selectPlaceOnMapComponent: SelectPlaceOnMapComponent? = null
    var searchPlaceComponent: SearchPlaceComponent? = null
    var refillTroikaComponent: RefillTroikaComponent? = null
    var refillStrelkaComponent: RefillStrelkaComponent? = null
    var refillTravelCardComponent: RefillTravelCardComponent? = null
    var routeDetailComponent: RouteDetailComponent? = null
    var introComponent: IntroComponent? = null
    var chooseAppComponent: ChooseAppComponent? = null
    var taxiOrderComponent: TaxiOrderComponent? = null
    var authFlowComponent: AuthFlowComponent? = null
    var enterPhoneComponent: EnterPhoneComponent? = null
    var verifyPhoneComponent: VerifyPhoneComponent? = null
    var registrationComponent: RegistrationComponent? = null
    var profileComponent: ProfileComponent? = null
    var passengersComponent: PassengersComponent? = null
    var placeSelectComponent: PlaceSelectComponent? = null

    // --- Tickets ---
    var searchTicketsComponent: SearchTicketsComponent? = null
        get() {
            if (field == null) {
                initSearchTicketsComponent()
            }
            return field
        }

    var tripGroupComponent: TripGroupComponent? = null
    var basketComponent: BasketComponent? = null
    var segmentsComponent: SegmentsComponent? = null
    var dateSelectComponent: DateSelectComponent? = null
    var mapComponent: MapComponent? = null

    fun initAppComponent(appComponent: AppComponent) {
        this.appComponent = appComponent
    }

    fun init(tag: String) {
        when (tag) {
            Screens.Main.TAG -> initMainComponent()
            Screens.Chat.TAG -> initChatComponent()
            Screens.SelectPlaceOnMap.TAG -> initSelectPlaceOnMapComponent()
            Screens.PlaceSearch.TAG -> initPlaceSearchComponent()
            Screens.RefillTroika.TAG -> initRefillTroikaComponent()
            Screens.RefillStrelka.TAG -> initRefillStrelkaComponent()
            Screens.RouteDetail.TAG -> initRouteDetailComponent()
            Screens.Intro.TAG -> initIntroComponent()
            Screens.RefillTravelCardDialog.TAG -> initRefillTravelCardComponent()
            Screens.ChooseAppDialog.TAG -> initChooseAppComponent()
            Screens.TaxiOrderDialog.TAG -> initTaxiOrderComponent()
            Screens.AuthFlow.TAG -> initAuthFlowComponent()
            Screens.EnterPhone.TAG -> initEnterPhoneComponent()
            Screens.VerifyPhone.TAG -> initVerifyPhoneComponent()
            Screens.Profile.TAG -> initProfileComponent()
            Screens.Registration.TAG -> initRegistrationComponent()
            Screens.Map.TAG -> initMapComponent()
            // --- Tickets ---
            Screens.SearchTickets.TAG -> initSearchTicketsComponent()
            Screens.DateSelect.TAG -> initDateSelectComponent()
            Screens.Passengers.TAG -> initPassengersComponent()
            Screens.PlaceSelect.TAG -> initPlaceSelectComponent()
            Screens.TripGroupScreen.TAG -> initTripGroupComponent()
            Screens.Basket.TAG -> initBasketComponent()
            Screens.Segments.TAG -> initSegmentsComponent()
        }
    }

    fun destroy(tag: String) {
        when (tag) {
            Screens.Main.TAG -> mainComponent = null
            Screens.Chat.TAG -> chatComponent = null
            Screens.SelectPlaceOnMap.TAG -> selectPlaceOnMapComponent = null
            Screens.PlaceSearch.TAG -> searchPlaceComponent = null
            Screens.RefillTroika.TAG -> refillTroikaComponent = null
            Screens.RefillStrelka.TAG -> refillStrelkaComponent = null
            Screens.RouteDetail.TAG -> routeDetailComponent = null
            Screens.Intro.TAG -> introComponent = null
            Screens.RefillTravelCardDialog.TAG -> refillTravelCardComponent = null
            Screens.ChooseAppDialog.TAG -> chooseAppComponent = null
            Screens.TaxiOrderDialog.TAG -> taxiOrderComponent = null
            Screens.AuthFlow.TAG -> authFlowComponent = null
            Screens.EnterPhone.TAG -> enterPhoneComponent = null
            Screens.VerifyPhone.TAG -> verifyPhoneComponent = null
            Screens.Profile.TAG -> profileComponent = null
            Screens.Registration.TAG -> registrationComponent = null
            Screens.Map.TAG -> mapComponent = null
            // --- Tickets ---
            Screens.SearchTickets.TAG -> searchTicketsComponent = null
            Screens.DateSelect.TAG -> dateSelectComponent = null
            Screens.Passengers.TAG -> passengersComponent = null
            Screens.PlaceSelect.TAG -> placeSelectComponent = null
            Screens.TripGroupScreen.TAG -> tripGroupComponent = null
            Screens.Basket.TAG -> basketComponent = null
            Screens.Segments.TAG -> segmentsComponent = null
        }
    }

    private fun initMainComponent() {
        mainComponent = DaggerMainComponent.builder()
            .appComponent(appComponent)
            .build()
    }

    private fun initChatComponent() {
        if (chatComponent == null) {
            chatComponent = DaggerChatComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initSelectPlaceOnMapComponent() {
        if (selectPlaceOnMapComponent == null) {
            selectPlaceOnMapComponent = DaggerSelectPlaceOnMapComponent.builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initPlaceSearchComponent() {
        if (searchPlaceComponent == null) {
            searchPlaceComponent = DaggerSearchPlaceComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initRefillTroikaComponent() {
        if (refillTroikaComponent == null) {
            refillTroikaComponent = DaggerRefillTroikaComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initRefillStrelkaComponent() {
        if (refillStrelkaComponent == null) {
            refillStrelkaComponent = DaggerRefillStrelkaComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initRouteDetailComponent() {
        if (routeDetailComponent == null) {
            routeDetailComponent = DaggerRouteDetailComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initIntroComponent() {
        if (introComponent == null) {
            introComponent = DaggerIntroComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initRefillTravelCardComponent() {
        if (refillTravelCardComponent == null) {
            refillTravelCardComponent = DaggerRefillTravelCardComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initChooseAppComponent() {
        if (chooseAppComponent == null) {
            chooseAppComponent = DaggerChooseAppComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initTaxiOrderComponent() {
        if (taxiOrderComponent == null) {
            taxiOrderComponent = DaggerTaxiOrderComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initAuthFlowComponent() {
        if (authFlowComponent == null) {
            authFlowComponent = DaggerAuthFlowComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initEnterPhoneComponent() {
        if (enterPhoneComponent == null) {
            enterPhoneComponent = DaggerEnterPhoneComponent
                .builder()
                .authFlowComponent(authFlowComponent)
                .build()
        }
    }

    private fun initVerifyPhoneComponent() {
        if (verifyPhoneComponent == null) {
            verifyPhoneComponent = DaggerVerifyPhoneComponent
                .builder()
                .authFlowComponent(authFlowComponent)
                .build()
        }
    }

    private fun initRegistrationComponent() {
        if (registrationComponent == null) {
            registrationComponent = DaggerRegistrationComponent
                .builder()
                .authFlowComponent(authFlowComponent)
                .build()
        }
    }

    private fun initProfileComponent() {
        if (profileComponent == null) {
            profileComponent = DaggerProfileComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initMapComponent() {
        if (mapComponent == null) {
            mapComponent = DaggerMapComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    // --- Tickets ---
    private fun initSearchTicketsComponent() {
        searchTicketsComponent = DaggerSearchTicketsComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
    }

    private fun initDateSelectComponent() {
        if (dateSelectComponent == null) {
            dateSelectComponent = DaggerDateSelectComponent
                .builder()
                .searchTicketsComponent(searchTicketsComponent)
                .build()
        }
    }

    private fun initPassengersComponent() {
        if (passengersComponent == null) {
            passengersComponent = DaggerPassengersComponent
                .builder()
                .searchTicketsComponent(searchTicketsComponent)
                .build()
        }
    }

    private fun initPlaceSelectComponent() {
        if (placeSelectComponent == null) {
            placeSelectComponent = DaggerPlaceSelectComponent
                .builder()
                .searchTicketsComponent(searchTicketsComponent)
                .build()
        }
    }

    private fun initTripGroupComponent() {
        if (tripGroupComponent == null) {
            tripGroupComponent = DaggerTripGroupComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initBasketComponent() {
        if (basketComponent == null) {
            basketComponent = DaggerBasketComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }

    private fun initSegmentsComponent() {
        if (segmentsComponent == null) {
            segmentsComponent = DaggerSegmentsComponent
                .builder()
                .mainComponent(mainComponent)
                .build()
        }
    }
}