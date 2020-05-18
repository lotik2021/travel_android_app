package ru.movista.presentation.taxiorder

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_taxi_tariff.view.*
import ru.movista.R
import ru.movista.presentation.viewmodel.TaxiTariff

class TaxiOrderAdapter(private val tariffs: List<TaxiTariff>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<TaxiOrderAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_taxi_tariff, parent, false))
    }

    override fun getItemCount() = tariffs.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(tariffs[position])
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bind(tariff: TaxiTariff) {
            itemView.item_taxi_tariff_name.text = tariff.taxiTariffName
            itemView.item_taxi_tariff_price.text = "${tariff.taxiTariffPrice}"
        }
    }

    class TaxiTariffsMarginItemDecoration(private val startMargin: Int, private val endMargin: Int) :
        androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: androidx.recyclerview.widget.RecyclerView,
            state: androidx.recyclerview.widget.RecyclerView.State
        ) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = startMargin
            }

            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                outRect.right = endMargin
            }
        }
    }
}