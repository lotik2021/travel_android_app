package ru.movista.presentation.carnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.movista.R
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.viewmodel.CarRouteViewModel

class CarNavigationDialogFragment : BaseDialogFragment() {

    companion object {
        private const val KEY_ROUTE_CAR = "car_route"

        fun getInstance(carRouteViewModel: CarRouteViewModel): CarNavigationDialogFragment {
            val instance = CarNavigationDialogFragment()

            val bundle = Bundle()
            bundle.putSerializable(KEY_ROUTE_CAR, carRouteViewModel)
            instance.arguments = bundle

            return instance
        }
    }

    private lateinit var carNavigationViewDelegate: CarNavigationViewDelegate


    private lateinit var carRouteViewModel: CarRouteViewModel

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments ?: return
        carRouteViewModel = bundle.getSerializable(KEY_ROUTE_CAR) as CarRouteViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_car_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carNavigationViewDelegate =
            CarNavigationViewDelegate(
                view,
                carRouteViewModel,
                childFragmentManager
            ).apply { initUI() }
    }
}