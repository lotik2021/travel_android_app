package ru.movista.presentation.placesearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_search_place.view.*
import ru.movista.R
import ru.movista.presentation.utils.inject
import ru.movista.presentation.viewmodel.SearchPlaceViewModel

class SearchPlaceAdapter(private val onClickListener: (Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SearchPlaceAdapter.ViewHolder>() {

    var items = listOf<SearchPlaceViewModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_place, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener { onClickListener.invoke(adapterPosition) }
        }

        fun bind(searchPlaceViewModel: SearchPlaceViewModel) {
            with(itemView) {
                search_place_icon.inject(searchPlaceViewModel.icon, searchPlaceViewModel.iosIcon)
                search_place_title.text = searchPlaceViewModel.title
                search_place_text.text = searchPlaceViewModel.text
            }
        }
    }
}