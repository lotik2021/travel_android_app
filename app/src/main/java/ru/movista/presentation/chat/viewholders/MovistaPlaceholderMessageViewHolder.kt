package ru.movista.presentation.chat.viewholders

import android.view.View
import kotlinx.android.synthetic.main.item_movista_placeholder.view.*
import ru.movista.presentation.chat.MovistaPlaceholderItem

class MovistaPlaceholderMessageViewHolder(view: View, action: (Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    init {
        itemView.movista_placeholder_choose_button.setOnClickListener {
            action.invoke(adapterPosition)
        }
    }

    fun bind(movistaPlaceholderItem: MovistaPlaceholderItem) {
        with(itemView) {
            movista_placeholder_hint.text = movistaPlaceholderItem.placeholder.optionsCountHint
        }
    }
}