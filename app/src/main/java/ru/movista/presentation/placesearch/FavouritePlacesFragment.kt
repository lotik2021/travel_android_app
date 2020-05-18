package ru.movista.presentation.placesearch

import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favourite_places.*
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.viewmodel.UserPlacesViewModel

class FavouritePlacesFragment : BaseFragment(), UserHistoryView {

    lateinit var adapter: HistoryPlacesAdapter

    override fun getLayoutRes() = R.layout.fragment_favourite_places

    override fun showUserHistory(historyItems: List<UserPlacesViewModel>, listener: (Int) -> Unit) {
        adapter = HistoryPlacesAdapter(historyItems, listener)
        favourite_places_recycler_view.layoutManager = LinearLayoutManager(activity)
        favourite_places_recycler_view.setHasFixedSize(true)
        favourite_places_recycler_view.adapter = adapter
    }
}
