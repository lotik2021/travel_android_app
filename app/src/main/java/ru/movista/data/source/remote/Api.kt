package ru.movista.data.source.remote

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*
import ru.movista.data.entity.*
import ru.movista.data.entity.tickets.*

interface Api {

    @POST("auth/create")
    fun getToken(
        @Body
        param: Map<String, String>
    ): Single<ResponseBody>

    @POST("search/autocomplete")
    fun search(
        @Header("Authorization")
        token: String,
        @Body
        params: SearchPlaceRequest
    ): Single<SearchPlaceResponse>

    @POST("search/places/{place_id}")
    fun getPlaceByID(
        @Header("Authorization")
        token: String,
        @Path("place_id")
        placeID: String,
        @Body
        params: GetPlaceByIDRequest
    ): Single<GetPlaceByIDResponse>

    @POST("user/pay/troika")
    fun payWithTroika(
        @Header("Authorization")
        token: String,
        @Body
        params: PayWithTroikaRequest
    ): Single<ResponseBody>

    @POST("user/pay/strelka/paymentparameters")
    fun getStrelkaParams(
        @Header("Authorization")
        token: String,
        @Body
        cardNumber: StrelkaBalanceRequest
    ): Single<StrelkaBalanceResponse>

    @POST("user/pay/strelka")
    fun payWithStrelka(
        @Header("Authorization")
        token: String,
        @Body
        params: PayWithStrelkaRequest
    ): Single<ResponseBody>


    @POST("dialogs/dialog")
    fun postAction(
        @Header("Authorization")
        token: String,
        @Body
        params: ActionRequest
    ): Single<DialogResponse>

    @POST("dialogs/createSession")
    fun createSession(
        @Header("Authorization")
        token: String,
        @Body
        params: CreateSessionRequest
    ): Single<DialogResponse>

    @POST("user/pay/travelcards")
    fun getAvailableTravelCards(
        @Header("Authorization")
        token: String
    ): Single<AvailableTravelCardsResponse>

    @POST("user/ping/location")
    fun pingLocation(
        @Header("Authorization")
        token: String,
        @Body
        params: PingLocationRequest
    ): Completable

    @POST("user/deeplink/click")
    fun reportTaxiOrder(
        @Header("Authorization")
        token: String,
        @Body
        params: TaxiOrderReportRequest
    ): Completable

    @POST("search/places/history")
    fun getRecentPlaces(
        @Header("Authorization")
        token: String,
        @Body
        params: RecentPlacesRequest
    ): Single<RecentPlacesResponse>

    @POST("search/places/favorite")
    fun getFavouritePlaces(
        @Header("Authorization")
        token: String,
        @Body
        params: FavouritePlacesRequest
    ): Single<FavouritePlacesResponse>

    @POST("auth/sendSmsMobile")
    fun authorizeWithPhoneNumber(
        @Header("Authorization")
        token: String,
        @Body
        params: PhoneAuthRequest
    ): Single<PhoneAuthResponse>

    @POST("auth/authorize")
    fun verifyPhoneNumber(
        @Header("Authorization")
        token: String,
        @Body
        params: VerifyPhoneRequest
    ): Single<VerifyPhoneResponse>

    @POST("auth/regenerateCodeMobile")
    fun regenerateCode(
        @Header("Authorization")
        token: String,
        @Body
        params: RegenerateCodeRequest
    ): Single<RegenerateCodeResponse>

    @POST("auth/completeRegistration")
    fun registerUser(
        @Header("Authorization")
        token: String,
        @Body
        params: RegistrateUserRequest
    ): Single<RegistrateUserResponse>

    @POST("user/getprofile")
    fun getUserProfile(
        @Header("Authorization")
        token: String
    ): Single<GetUserProfileResponse>

    @POST("user/updateusergoogletransports")
    fun updateUserTransportTypes(
        @Header("Authorization")
        token: String,
        @Body
        params: UpdateUserTransportsRequest
    ): Single<UpdateUserTransportsResponse>

    @POST("user/favorites/delete/{id}")
    fun deleteUserFavoritesPlaces(
        @Header("Authorization")
        token: String,
        @Path("id")
        placeId: Long
    ): Single<EmptyResponse>

    @POST("user/favorites/create")
    fun createFavoritesPlaces(
        @Header("Authorization")
        token: String,
        @Body
        params: UserFavoritePlacesRequest
    ): Single<CreateUserFavoritesResponse>

    @PUT("user/favorites/{id}")
    fun updateFavoritesPlaces(
        @Header("Authorization")
        token: String,
        @Path("id")
        id: Long,
        @Body params:
        UserFavoritePlacesRequest
    ): Single<EmptyResponse>

//    @GET("user/favorites")
//    fun getUserFavoritesPlaces(
//        @Header("Authorization")
//        token: String,
//        @Body
//        params: FavouritePlacesRequest
//    ): Single<FavouritePlacesResponse>

    // ---- Tickets ----
    @POST("search/places/findByName")
    fun searchPlacesByName(
        @Header("Authorization") token: String,
        @Body params: SearchPlacesRequest
    ): Single<SearchPlacesResponse>

    // --- ONESIGNAL ---
    @POST("user/setosplayerid")
    fun setOsPlayerId(
        @Header("Authorization")
        token: String,
        @Body
        params: OnesignalPlayerRequest
    ): Single<EmptyResponse>

    @POST("/api/search/searchAsync")
    fun asyncTicketsSearch(
        @Header("Authorization") token: String,
        @Body params: AsyncSearchRequest
    ): Single<AsyncSearchResponse>

    @POST("/api/search/getSearchStatus")
    fun getSearchStatus(
        @Header("Authorization") token: String,
        @Body request: SearchStatusRequest
    ): Observable<AsyncSearchResponse>

    @POST("/api/search/getSearchResults")
    fun getSearchResults(
        @Header("Authorization") token: String,
        @Body request: SearchStatusRequest
    ): Observable<SearchResultsResponse>

    @POST("/api/search/getPathGroup")
    fun getPathGroup(
        @Header("Authorization") token: String,
        @Body request: PathGroupRequest
    ): Single<PathGroupResponse>

    @POST("/api/search/getSegmentRoutes")
    fun getSegmentRoutes(
        @Header("Authorization") token: String,
        @Body request: SegmentRoutesRequest
    ): Single<SegmentRoutesResponse>

    @POST("/api/search/saveselectedroutes")
    fun saveSelectedRoutes(
        @Header("Authorization") token: String,
        @Body request: SaveSelectedRoutesRequest
    ): Single<SaveSelectedRoutesResponse>

    @POST("/api/search/asyncAvailable")
    fun asyncAvailable(@Header("Authorization") token: String): Single<AsyncAvailableResponse>
}