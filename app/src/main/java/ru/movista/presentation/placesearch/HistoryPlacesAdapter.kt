package ru.movista.presentation.placesearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.android.synthetic.main.item_search_place.view.*
import ru.movista.R
import ru.movista.presentation.utils.inject
import ru.movista.presentation.viewmodel.UserPlacesViewModel

class HistoryPlacesAdapter(
    private val items: List<UserPlacesViewModel>,
    private val onClickListener: (Int) -> Unit
) : Adapter<HistoryPlacesAdapter.HistoryPlacesViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryPlacesViewHolder {
        return HistoryPlacesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_place, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: HistoryPlacesViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    inner class HistoryPlacesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener { onClickListener.invoke(adapterPosition) }
        }

        fun bind(userPlacesViewModel: UserPlacesViewModel) {
            with(itemView) {
                search_place_icon.inject(userPlacesViewModel.icon, userPlacesViewModel.iosIcon)
                search_place_title.text = userPlacesViewModel.title
                search_place_text.text = userPlacesViewModel.text
            }
        }
    }
}