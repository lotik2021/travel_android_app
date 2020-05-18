package ru.movista.presentation.placesearch

import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recent_places.*
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.viewmodel.UserPlacesViewModel

class RecentPlacesFragment : BaseFragment(), UserHistoryView {

    private lateinit var adapter: HistoryPlacesAdapter

    override fun getLayoutRes() = R.layout.fragment_recent_places

    override fun showUserHistory(historyItems: List<UserPlacesViewModel>, listener: (Int) -> Unit) {
        adapter = HistoryPlacesAdapter(historyItems, listener)
        recent_places_recycler_view.layoutManager = LinearLayoutManager(activity)
        recent_places_recycler_view.setHasFixedSize(true)
        recent_places_recycler_view.adapter = adapter
    }
}
