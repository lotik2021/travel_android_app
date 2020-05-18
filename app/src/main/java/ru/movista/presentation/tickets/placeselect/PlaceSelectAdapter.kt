package ru.movista.presentation.tickets.placeselect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_select_place.view.*
import org.jetbrains.anko.imageResource
import ru.movista.R
import ru.movista.domain.model.tickets.Place
import ru.movista.presentation.utils.getImagePathFromResourceName

class PlaceSelectAdapter(private val onClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<PlaceSelectAdapter.ViewHolder>() {

    var items = listOf<Place>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_select_place,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener { onClickListener.invoke(adapterPosition) }
        }

        fun bind(place: Place) {
            with(itemView) {
                val iconPath = resources.getImagePathFromResourceName(place.iconName)
                iconPath?.let { select_place_icon.imageResource = it }

                select_place_title.text = place.name
                select_place_text.text = place.description
            }
        }
    }
}