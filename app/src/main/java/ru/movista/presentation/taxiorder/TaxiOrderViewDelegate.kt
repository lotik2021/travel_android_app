package ru.movista.presentation.taxiorder

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_taxi_order.view.*
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.repository.TaxiOrderRepository
import ru.movista.presentation.utils.inject
import ru.movista.presentation.utils.openInCustomBrowser
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.presentation.viewmodel.TaxiProviderViewModel
import ru.movista.utils.reportNullFieldError
import ru.movista.utils.schedulersIoToMain

class TaxiOrderViewDelegate(
    private val layout: View,
    private val taxiOrderRepository: TaxiOrderRepository,
    private val taxiProviderViewModel: TaxiProviderViewModel,
    private val analyticsManager: AnalyticsManager?,
    private val taxiOrderDialogFragment: TaxiOrderDialogFragment? = null
) {

    private val resources = layout.resources
    private val context = layout.context

    private var taxiOrderAdapter: TaxiOrderAdapter? = null

    fun initUI() {
        layout.taxi_order_tariffs_recycler_view.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        layout.taxi_order_tariffs_recycler_view.addItemDecoration(
            TaxiOrderAdapter.TaxiTariffsMarginItemDecoration(
                resources.getDimensionPixelOffset(
                    R.dimen.size_medium
                ), resources.getDimensionPixelOffset(R.dimen.size_medium)
            )
        )

        layout.taxi_order_image.inject(
            taxiProviderViewModel.taxiProviderImage,
            taxiProviderViewModel.taxiProviderIosImage
        )

        layout.taxi_order_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(taxiProviderViewModel.taxiProviderLink)

            context?.let {
                taxiOrderDialogFragment?.closeSelf()

                taxiOrderRepository.reportTaxiOrder(
                    taxiProviderViewModel.taxiProviderDeeplinkId,
                    taxiProviderViewModel.taxiProviderLink
                )
                    .schedulersIoToMain()
                    .subscribe {
                        // отправляем на сервер событие по переходу на такси
                    }

                postOnMainThread {
                    if (intent.resolveActivity(it.packageManager) != null) {
                        it.startActivity(intent)
                    } else {
                        it.openInCustomBrowser(taxiProviderViewModel.taxiProviderStoreLink)
                    }
                    analyticsManager?.reportTaxiDetailsOrder(taxiProviderViewModel.taxiProviderDescription)
                }

            } ?: reportNullFieldError("context")
        }

        layout.taxi_order_from_to_time.text =
            "${taxiProviderViewModel.taxiProviderStartTime} - ${taxiProviderViewModel.taxiProviderEndTime}"

        taxiOrderAdapter = TaxiOrderAdapter(taxiProviderViewModel.taxiProviderTariffs)
        layout.taxi_order_tariffs_recycler_view.adapter = taxiOrderAdapter
    }

    fun onDestroyView() {
        layout.taxi_order_tariffs_recycler_view.adapter = null
    }
}