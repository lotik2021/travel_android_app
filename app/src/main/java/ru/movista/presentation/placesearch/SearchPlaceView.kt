package ru.movista.presentation.placesearch

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.presentation.viewmodel.SearchPlaceViewModel
import ru.movista.presentation.viewmodel.UserPlacesViewModel

interface SearchPlaceView : MvpView {

    fun showSearchResult(searchPlaceViewModelList: List<SearchPlaceViewModel>)

    fun hideKeyboard()

    fun showNoResults()

    fun showCommonError()

    fun hideLoading()

    fun showPreviousResultsLoading()
    fun hidePreviousResultsLoading()

    fun showPreviousResults()
    fun hidePreviousResults()

    fun showUserFavourites(results: List<UserPlacesViewModel>)
    fun showUserRecent(results: List<UserPlacesViewModel>)

    @StateStrategyType(SkipStrategy::class)
    fun showMsg(msg: String)

    @StateStrategyType(SkipStrategy::class)
    fun showFavoriteNameAlert(placeHolder: String)

    fun hideHistoryViewPager()
    fun showContentLoading()
    fun hideContentLoading()
    fun setSearchPlaceResultsVisibility(visible: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCurrentPage(userHistoryTab: UserHistoryTab)
}