package ru.movista.presentation.taxiorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.movista.R
import ru.movista.data.repository.TaxiOrderRepository
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.viewmodel.TaxiProviderViewModel
import javax.inject.Inject

class TaxiOrderDialogFragment : BaseDialogFragment() {

    companion object {
        private const val TAG = Screens.TaxiOrderDialog.TAG
        private const val TAXI_PROVIDER = "taxi_provider"

        fun getInstance(taxiProvider: TaxiProviderViewModel): TaxiOrderDialogFragment {
            val instance = TaxiOrderDialogFragment()

            val bundle = Bundle()
            bundle.putSerializable(TAXI_PROVIDER, taxiProvider)
            instance.arguments = bundle

            return instance
        }
    }

    private lateinit var taxiOrderViewDelegate: TaxiOrderViewDelegate

    private lateinit var taxiProvider: TaxiProviderViewModel

    @Inject
    lateinit var taxiOrderRepository: TaxiOrderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.init(TAG)
        Injector.taxiOrderComponent?.inject(this)

        super.onCreate(savedInstanceState)

        val bundle = arguments ?: return
        taxiProvider = bundle.getSerializable(TAXI_PROVIDER) as TaxiProviderViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_taxi_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taxiOrderViewDelegate = TaxiOrderViewDelegate(
            view,
            taxiOrderRepository,
            taxiProvider,
            analyticsManager,
            this
        ).apply { initUI() }
    }

    override fun onDestroyView() {
        if (this::taxiOrderViewDelegate.isInitialized) taxiOrderViewDelegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        Injector.destroy(TAG)
        super.onDestroy()
    }

    fun closeSelf() {
        this.dismiss()
    }
}