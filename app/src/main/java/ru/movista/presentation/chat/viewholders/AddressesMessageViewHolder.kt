package ru.movista.presentation.chat.viewholders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_chat_message_address.view.*
import org.jetbrains.anko.bottomPadding
import ru.movista.R
import ru.movista.presentation.chat.AddressesItem
import ru.movista.presentation.utils.inject
import ru.movista.presentation.utils.setGone
import ru.movista.utils.inflate

class AddressesMessageViewHolder(view: View, val listener: (Int, Int) -> Unit) : RecyclerView.ViewHolder(view) {

    fun bind(addressesItem: AddressesItem) {
        val context = itemView.context

        val parentLayout = itemView as LinearLayout
        parentLayout.removeAllViews()

        addressesItem.addresses.forEachIndexed { index, item ->
            val view: View = context.inflate(R.layout.layout_chat_message_address)

            with(view) {
                chat_address_name.text = item.name
                chat_address_description.text = item.description
                chat_address_icon.inject(item.icon, item.iosIcon)

                setOnClickListener { listener.invoke(adapterPosition, index) }

                if (index == addressesItem.addresses.lastIndex) {
                    chat_address_divider.setGone()
                    chat_address_description.bottomPadding =
                        context.resources.getDimensionPixelSize(R.dimen.size_medium)
                }
                parentLayout.addView(this)
            }
        }
    }
}