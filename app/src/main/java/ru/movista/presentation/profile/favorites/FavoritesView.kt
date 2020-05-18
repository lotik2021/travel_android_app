package ru.movista.presentation.profile.favorites

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.domain.model.FavoritePlace

@StateStrategyType(SkipStrategy::class)
interface FavoritesView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setLoaderVisibility(visible: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setFavoritesContentVisibility(visible: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addOtherFavorite(favorites: FavoritePlace)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun updateHome(homePlace: FavoritePlace?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun updateWork(workPlace: FavoritePlace?)

    fun updateItems(homePlace: FavoritePlace?, workPlace: FavoritePlace?, favorites: List<FavoritePlace>)
    fun removeOtherFavorite(favoritePlace: FavoritePlace)
    fun showMsg(msg: String)
}