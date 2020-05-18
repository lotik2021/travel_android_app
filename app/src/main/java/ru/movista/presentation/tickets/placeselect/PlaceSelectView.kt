package ru.movista.presentation.tickets.placeselect

import android.view.View
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.movista.domain.model.tickets.Place
import ru.movista.presentation.base.BaseLoadingView

interface PlaceSelectView : BaseLoadingView {

    fun setToolbarText(fromText: String, toText: String)

    fun showPlacesFound(places: List<Place>)

    fun showRecyclerView()
    fun hideRecyclerView()

    fun clearEditText()

    fun hideKeyboard()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMsg(message: String, action: View.OnClickListener)

    @StateStrategyType(SkipStrategy::class)
    fun hideErrorMsg()
}