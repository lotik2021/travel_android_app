package ru.movista.presentation.carnavigation

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_car_navigation.view.*
import ru.movista.R
import ru.movista.presentation.chooseapp.ChooseAppDialogFragment
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.viewmodel.CarDeeplinkViewModel
import ru.movista.presentation.viewmodel.CarRouteViewModel
import ru.movista.utils.reportNullFieldError
import timber.log.Timber

class CarNavigationViewDelegate(
    private val layout: View,
    private val carRouteViewModel: CarRouteViewModel,
    private val fragmentManager: FragmentManager
) {

    companion object {
        private const val GOOGLE_NAVIGATION_APP = "google"

        private const val KEY_CHOOSE_APP_BOTTOM_DIALOG = "car_navigation"
    }

    private val resources = layout.resources

    private var currentCarRouteDeeplinks: List<CarDeeplinkViewModel>? = null

    fun initUI() {
        layout.trip_car_duration_and_distance.text = resources.getString(
            R.string.approximately_duration_and_distance,
            carRouteViewModel.carMaxDuration,
            carRouteViewModel.carDistance
        )
        layout.trip_car_max_duration.text = carRouteViewModel.carMaxDuration
        layout.trip_car_min_duration.text = carRouteViewModel.carMinDuration

        if (carRouteViewModel.carRouteThrough.isNotEmpty()) {
            layout.trip_car_group_route_through.setVisible()
            layout.trip_car_route_through.text = carRouteViewModel.carRouteThrough
        } else {
            layout.trip_car_group_route_through.setGone()
        }

        layout.start_navigation_button.setOnClickListener {
            currentCarRouteDeeplinks = carRouteViewModel.carDeeplinks
            Timber.d("All deeplinks: $currentCarRouteDeeplinks")

            val dangerousDeeplinks = arrayListOf<CarDeeplinkViewModel>()
            carRouteViewModel.carDeeplinks.forEach {
                // проверим все прилежения, кроме гугл (если его нет на устройстве - маршрут откроется в браузере)
                if (!it.carDeepLinkUrl.contains(
                        GOOGLE_NAVIGATION_APP,
                        true
                    )
                ) dangerousDeeplinks.add(it)
            }
            Timber.d("Dangerous deeplinks: $dangerousDeeplinks")

            filterAppsThatAreNotInstalled(dangerousDeeplinks)
        }
    }

    private fun filterAppsThatAreNotInstalled(carDeeplinksViewModel: ArrayList<CarDeeplinkViewModel>) {
        val deeplinksCantBeHandled = arrayListOf<CarDeeplinkViewModel>()

        carDeeplinksViewModel.forEach { carDeeplinkViewModel ->

            val intent = Intent(Intent.ACTION_VIEW)

            intent.data = Uri.parse(carDeeplinkViewModel.carDeepLinkUrl)

            layout.context?.let {
                if (intent.resolveActivity(it.packageManager) == null) {
                    deeplinksCantBeHandled.add(carDeeplinkViewModel)
                }
            } ?: reportNullFieldError("context")
        }
        onAppsThatAreNotInstalledFiltered(deeplinksCantBeHandled)
    }

    private fun onAppsThatAreNotInstalledFiltered(deeplinksCantBeHandled: List<CarDeeplinkViewModel>) {
        Timber.d("onAppsThatAreNotInstalledFiltered")

        val filteredDeeplinks = arrayListOf<CarDeeplinkViewModel>()
        currentCarRouteDeeplinks?.forEach {
            filteredDeeplinks.add(it)
        }
        filteredDeeplinks.removeAll(deeplinksCantBeHandled)

        currentCarRouteDeeplinks = filteredDeeplinks
        Timber.d("Filtered deeplinks: $currentCarRouteDeeplinks")

        showChooseAppDialog(filteredDeeplinks)
    }


    private fun showChooseAppDialog(carDeeplinksViewModel: ArrayList<CarDeeplinkViewModel>) {
        val chooseAppDialogFragment = ChooseAppDialogFragment.getInstance(carDeeplinksViewModel)
        chooseAppDialogFragment.show(
            fragmentManager,
            KEY_CHOOSE_APP_BOTTOM_DIALOG
        )
    }
}