package ru.movista.presentation.placesearch

import io.reactivex.disposables.CompositeDisposable
import moxy.InjectViewState
import ru.movista.data.entity.ApiError
import ru.movista.data.entity.SearchPlaceResponse
import ru.movista.data.entity.SearchPlaceResponseItem
import ru.movista.di.Injector
import ru.movista.domain.model.FavoritePlaceType
import ru.movista.domain.model.Place
import ru.movista.domain.model.PlaceSearchHistoryItem
import ru.movista.domain.model.UserPlacesHistory
import ru.movista.domain.usecase.PlaceFoundUseCase
import ru.movista.domain.usecase.PreviousSearchPlaceResultsUseCase
import ru.movista.domain.usecase.SearchPlaceUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.converter.PlaceSearchResultConverter
import ru.movista.presentation.converter.PreviousSearchPlaceResultsConverter
import ru.movista.presentation.utils.postOnMainThreadWithDelay
import ru.movista.presentation.viewmodel.FavoriteModeViewModel
import ru.movista.presentation.viewmodel.SearchPlaceViewModel
import ru.movista.utils.schedulersIoToMain
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SearchPlacePresenter(
    private val favoriteMode: FavoriteModeViewModel?,
    private val userHistoryTab: UserHistoryTab
) : BasePresenter<SearchPlaceView>() {

    companion object {
        private const val SEARCH_NO_RESULTS = "7000"
    }

    override val screenTag: String
        get() = Screens.PlaceSearch.TAG

    private lateinit var currentSearchQuery: String

    private var currentSearchResult = listOf<SearchPlaceResponseItem>()

    private lateinit var searchPlaceUseCase: SearchPlaceUseCase
    private lateinit var placeFoundUseCase: PlaceFoundUseCase

    private var previousSearchPlaceResultsDisposable = CompositeDisposable()

    private lateinit var searchHistory: UserPlacesHistory
    private lateinit var chosenPlace: SearchPlaceResponseItem

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var previousSearchPlaceResultsUseCase: PreviousSearchPlaceResultsUseCase

    @Inject
    lateinit var previousSearchPlaceResultsConverter: PreviousSearchPlaceResultsConverter

    @Inject
    lateinit var placeSearchResultConverter: PlaceSearchResultConverter

    override fun onPresenterInject() {
        Injector.searchPlaceComponent?.inject(this)
        Injector.searchPlaceComponent?.getSearchPlaceUseCase()?.let {
            searchPlaceUseCase = it
        }
            ?: Timber.e("Illegal state: searchPlaceComponent: ${Injector.searchPlaceComponent}, getSearchPlaceUseCase: ${Injector.searchPlaceComponent?.getSearchPlaceUseCase()}")

        Injector.searchPlaceComponent?.getPlaceFoundUseCase()?.let {
            placeFoundUseCase = it
        }
            ?: Timber.e("Illegal state: searchPlaceComponent: ${Injector.searchPlaceComponent}, getPlaceFoundUseCase: ${Injector.searchPlaceComponent?.getPlaceFoundUseCase()}")
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (isSearchFavoriteMode()) {
            viewState.hideHistoryViewPager()
        } else {
            viewState.setCurrentPage(userHistoryTab)
        }
    }

    // срабатывает сразу при подписке
    fun onSearchTextChanged(text: String) {
        if (text.isEmpty()) {
            if (!this::searchHistory.isInitialized && !isSearchFavoriteMode()) {
                // если еще не загрузили историю - загружаем (если же загрузили - она уже отображаетмся во вью)
                requestPreviousResults()
            }
            return
        }

        currentSearchQuery = text
        previousSearchPlaceResultsDisposable.clear() // отменяем запрос истории поиска

        doSearchRequest()
    }

    fun onRetryClicked() {
        doSearchRequest()
    }

    fun onBackClicked() {
        closeKeyboardAndExit()
        placeFoundUseCase.onFindCanceled()
    }

    fun onPlaceClicked(index: Int) {
        if (index > currentSearchResult.lastIndex) {
            Timber.e("Index $index is greater than currentSearchResult last index")
            return
        }

        chosenPlace = currentSearchResult[index]

        if (favoriteMode != null) {
            when {
                favoriteMode.placeType == FavoritePlaceType.NONE -> viewState.showFavoriteNameAlert(chosenPlace.main_text)
                favoriteMode.homeWorkPlaceId != null -> updateFavorite(
                    chosenPlace,
                    favoriteMode.placeType,
                    favoriteMode.homeWorkPlaceId
                )
                else -> createFavorite(chosenPlace, favoriteMode.placeType) // создание home-work
            }
        } else {
            searchPlace(chosenPlace)
        }
    }

    fun onEnterNameClick(favoritePlaceName: String) {
        favoriteMode?.let { createFavorite(chosenPlace, favoriteMode.placeType, favoritePlaceName) }
    }

    fun onHistoryPlaceClicked(selectedTab: UserHistoryTab, index: Int) {

        val chosenPlace: PlaceSearchHistoryItem? = when (selectedTab) {
            UserHistoryTab.FAVORITES -> {
                searchHistory.favourites.getOrNull(index)
            }

            UserHistoryTab.RECENT -> {
                searchHistory.recent.getOrNull(index)
            }
        }

        chosenPlace?.let {
            searchPlaceUseCase.sendChosenHistoryPlace(
                Place(it.placeName, it.placeDescription, it.placeLat, it.placeLon, it.placeId)
            )
            closeKeyboardAndExit()
        } ?: Timber.e("No match from searchHistory was found")

    }

    private fun isSearchFavoriteMode() = favoriteMode != null

    private fun searchPlace(chosenPlace: SearchPlaceResponseItem) {
        addDisposable(
            searchPlaceUseCase.getPlaceByID(chosenPlace.place_id, chosenPlace.main_text, chosenPlace.secondary_text)
                .schedulersIoToMain()
                .subscribe(
                    { closeKeyboardAndExit() },
                    { viewState.showCommonError() }
                )
        )
    }

    private fun createFavorite(
        chosenPlace: SearchPlaceResponseItem,
        placeType: FavoritePlaceType,
        placeName: String? = null
    ) {
        Injector.init(Screens.Profile.TAG)
        val profileUseCase = Injector.profileComponent?.getProfileUseCase()
        profileUseCase?.let { useCase ->
            addDisposable(
                searchPlaceUseCase.getPlace(chosenPlace.place_id, chosenPlace.main_text, chosenPlace.secondary_text)
                    .flatMap { place ->
                        useCase.createUserFavoritesPlaces(
                            place,
                            chosenPlace.types,
                            placeType,
                            placeName,
                            chosenPlace.android_icon_url,
                            chosenPlace.ios_icon_url_dark
                        )
                    }
                    .schedulersIoToMain()
                    .doOnSubscribe {
                        viewState.setSearchPlaceResultsVisibility(false)
                        viewState.showContentLoading()
                    }
                    .subscribe(
                        { favoritePlace ->
                            searchPlaceUseCase.sendChosenFavoritePlace(favoritePlace)
                            closeKeyboardAndExit()
                        },
                        { th ->
                            viewState.setSearchPlaceResultsVisibility(true)
                            viewState.hideContentLoading()
                            Timber.e("createUserFavoritesPlaces onError, ${th.message}")
                            viewState.showMsg(getErrorDescription(th))
                        }
                    )
            )
        }
    }

    private fun updateFavorite(chosenPlace: SearchPlaceResponseItem, placeType: FavoritePlaceType, placeId: Long) {
        Injector.init(Screens.Profile.TAG)
        val profileUseCase = Injector.profileComponent?.getProfileUseCase()
        profileUseCase?.let { useCase ->
            addDisposable(
                searchPlaceUseCase.getPlace(chosenPlace.place_id, chosenPlace.main_text, chosenPlace.secondary_text)
                    .flatMap { place ->
                        useCase.updateFavoritesPlaces(
                            place,
                            chosenPlace.types,
                            placeType,
                            placeId,
                            chosenPlace.android_icon_url,
                            chosenPlace.ios_icon_url_dark
                        )
                    }
                    .schedulersIoToMain()
                    .doOnSubscribe {
                        viewState.setSearchPlaceResultsVisibility(false)
                        viewState.showContentLoading()
                    }
                    .subscribe(
                        { favoritePlace ->
                            searchPlaceUseCase.sendChosenFavoritePlace(favoritePlace)
                            closeKeyboardAndExit()
                        },
                        { th ->
                            viewState.setSearchPlaceResultsVisibility(true)
                            viewState.hideContentLoading()
                            Timber.e("updateFavoritesPlaces onError, ${th.message}")
                            viewState.showMsg(getErrorDescription(th))
                        }
                    )
            )
        }
    }

    private fun requestPreviousResults() {
        Timber.d("requestPreviousResults")
        previousSearchPlaceResultsDisposable.add(
            previousSearchPlaceResultsUseCase.getUserPlacesHistory()
                .schedulersIoToMain()
                .doOnSubscribe {
                    viewState.showPreviousResults()
                    viewState.showPreviousResultsLoading()
                }
                .subscribe(
                    {
                        viewState.hidePreviousResultsLoading()

                        searchHistory = it

                        if (searchHistory.favourites.isNotEmpty() || searchHistory.recent.isNotEmpty()) {
                            with(placeSearchResultConverter) {
                                viewState.showUserRecent(toUserPlacesViewModel(it.recent))
                                viewState.showUserFavourites(toUserPlacesViewModel(it.favourites))
                            }
                        } else {
                            viewState.hidePreviousResults()
                        }
                    },
                    {
                        viewState.hidePreviousResults()
                    }
                )
        )
    }

    private fun doSearchRequest() {
        addDisposable(
            searchPlaceUseCase.searchPlace(currentSearchQuery)
                .schedulersIoToMain()
                .doAfterTerminate { viewState.hideLoading() }
                .subscribe(
                    {
                        currentSearchResult = it.result
                        val searchResult = toViewModel(it)
                        if (searchResult.isEmpty()) {
                            viewState.showNoResults()
                        } else {
                            viewState.showSearchResult(searchResult)
                        }
                    },
                    {
                        onSearchError(it)
                    }
                )
        )
    }

    private fun toViewModel(searchPlaceResponse: SearchPlaceResponse): List<SearchPlaceViewModel> {

        return placeSearchResultConverter.toSearchPlaceViewModel(searchPlaceResponse)
    }

    private fun onSearchError(error: Throwable) {
        when (error) {
            is ApiError -> when (error.code) {
                SEARCH_NO_RESULTS -> viewState.showNoResults()
                else -> viewState.showCommonError()
            }
            else -> viewState.showCommonError()
        }
    }

    private fun closeKeyboardAndExit() {
        viewState.hideKeyboard()
        postOnMainThreadWithDelay(125) { router.exit() }
    }
}