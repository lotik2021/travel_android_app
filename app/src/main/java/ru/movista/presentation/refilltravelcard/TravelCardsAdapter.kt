package ru.movista.presentation.refilltravelcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_travel_card.view.*
import ru.movista.R

class TravelCardsAdapter(
    private val travelCards: ArrayList<TravelCardViewModel>,
    private val clickListener: (Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<TravelCardsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_travel_card, parent, false))
    }

    override fun getItemCount() = travelCards.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(travelCards[position])
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener { clickListener.invoke(adapterPosition) }
        }

        fun bind(travelCardViewModel: TravelCardViewModel) {
            itemView.item_travel_card_title.text = travelCardViewModel.cardTitle
            itemView.item_travel_card_purpose.text = travelCardViewModel.cardPurpose
        }
    }
}