package ru.movista.presentation.agencies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_agency.view.*
import ru.movista.R
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.viewmodel.AgencyViewModel

class AgenciesAdapter(
    private val items: List<AgencyViewModel>,
    private val onUrlClickListener: (Int) -> Unit,
    private val onPhoneClickListener: (Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<AgenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_agency, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bind(agencyViewModel: AgencyViewModel) {
            itemView.agency_title.text = agencyViewModel.title

            if (agencyViewModel.url.isNotEmpty()) {
                itemView.agency_url.text = agencyViewModel.url
                itemView.agency_url.setOnClickListener { onUrlClickListener.invoke(adapterPosition) }
                itemView.agency_url.setVisible()
            } else {
                itemView.agency_url.setGone()
            }

            if (agencyViewModel.phoneNumber.isNotEmpty()) {
                itemView.agency_phone_number.text = agencyViewModel.phoneNumber
                itemView.agency_phone_number.setOnClickListener { onPhoneClickListener.invoke(adapterPosition) }
                itemView.agency_phone_number.setVisible()
            } else {
                itemView.agency_phone_number.setGone()
            }
        }
    }
}