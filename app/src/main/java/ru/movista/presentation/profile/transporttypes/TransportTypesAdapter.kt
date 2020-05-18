package ru.movista.presentation.profile.transporttypes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.item_profile.view.*
import ru.movista.R
import ru.movista.domain.model.ProfileTransportType
import ru.movista.presentation.utils.inject

class TransportTypesAdapter(
    private val action: (ProfileTransportType, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val ITEM_VIEW_TYPE = 1
        private const val HEADER_VIEW_TYPE = ITEM_VIEW_TYPE shl 1
    }

    private var transports: MutableList<ProfileTransportType> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW_TYPE -> HeaderHolder(inflater.inflate(R.layout.item_profile_header, parent, false))
            else -> ItemHolder(inflater.inflate(R.layout.item_profile, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_VIEW_TYPE else ITEM_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return if (transports.isNotEmpty()) transports.size + 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER_VIEW_TYPE) {
            return
        }

        val transportType = getItem(position)
        val itemHolder = (holder as ItemHolder)

        with(itemHolder.itemView) {
            item_image.inject(transportType.icon, transportType.iosIcon)
            item.text = transportType.title
            item.isChecked = transportType.isOn
        }
    }

    fun setItems(transportsTypes: List<ProfileTransportType>) {
        transports.addAll(transportsTypes)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int) = transports[position - 1]

    private inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.item.setOnCheckedChangeListener { _, isChecked ->
                if (adapterPosition != NO_POSITION) {
                    action.invoke(getItem(adapterPosition), isChecked)
                }
            }
        }
    }

    private class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}