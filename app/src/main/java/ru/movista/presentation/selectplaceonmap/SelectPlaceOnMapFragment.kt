package ru.movista.presentation.selectplaceonmap

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_map.*
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.jetbrains.anko.support.v4.toast
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.common.mapview.MapFragment
import ru.movista.presentation.utils.getPlaceByCoordinates
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.viewmodel.AddressViewModel
import ru.movista.utils.reportNullFieldError

class SelectPlaceOnMapFragment : BaseFragment(), SelectPlaceOnMapView, OnBackPressedListener {

    companion object {
        private const val SIMPLE_MAP_KEY = "simple_map_key"

        fun newInstance(isSimpleMap: Boolean) : Fragment {
            return SelectPlaceOnMapFragment().apply {
                arguments = bundleOf(SIMPLE_MAP_KEY to isSimpleMap)
            }
        }
    }

    private var mapFragment: MapFragment? = null

    @InjectPresenter
    lateinit var presenter: SelectPlaceOnMapPresenter

    @ProvidePresenter
    fun providePresenter(): SelectPlaceOnMapPresenter {
        return SelectPlaceOnMapPresenter(arguments?.getBoolean(SIMPLE_MAP_KEY) ?: false)
    }

    override fun sharePresenter(): BasePresenter<MvpView>? {
        return presenter as? BasePresenter<MvpView>
    }

    override fun getLayoutRes() = R.layout.fragment_map

    override fun onFragmentInject() {
        Injector.init(Screens.SelectPlaceOnMap.TAG)
        Injector.selectPlaceOnMapComponent?.inject(this)
    }

    override fun initUI() {
        super.initUI()

        back_button.setOnClickListener { presenter.onBackClicked() }
        select_place_on_map_done_button.setOnClickListener { presenter.onDoneBtnClicked() }
    }

    override fun afterCreate() {
        super.afterCreate()
        mapFragment = childFragmentManager.findFragmentById(R.id.select_place_on_map_map_fragment)
                as MapFragment
    }

    override fun requestAddressByLocation(lat: Double, lon: Double, maxResults: Int) {

        (activity as? Context)?.let { context ->
            context.getPlaceByCoordinates(lat, lon, maxResults) { presenter.onAddressReceived(it) }
        } ?: reportNullFieldError("activity")
    }

    override fun showAddress(address: AddressViewModel) {
        val extras =
            if (address.cityName.isEmpty()) address.countryName else "${address.cityName}, ${address.countryName}"

        select_place_on_map_cv.setVisible()
        select_place_on_map_name.text = address.address
        select_place_on_map_name_extras.text = extras
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun moveMap(lat: Double, lon: Double, withAnimation: Boolean) {
        if (withAnimation) {
            mapFragment?.googleMapHelper?.animateCamera(lat, lon)
        } else {
            mapFragment?.googleMapHelper?.moveCamera(lat, lon)
        }
    }

    override fun subscribeOnMapEvents() {
        subscribeOnMapIdle()
        subscribeOnMapMoved()
    }

    override fun showNoLocationError() {
        toast(R.string.error_no_location)
    }

    override fun hideAddressView() {
        select_place_on_map_cv?.setGone()
    }

    override fun stopMapRotating() {
        mapFragment?.stopMapRotatingCommand()
    }

    override fun hideSelectingContainer() {
        select_place_on_map_cv.setGone()
        map_pin.setGone()
    }

    private fun subscribeOnMapIdle() {
        mapFragment?.googleMapHelper?.setOnMapIdleListener { lat, lon ->
            presenter.onMapPositionChanged(lat, lon)
        }
    }

    private fun subscribeOnMapMoved() {
        mapFragment?.googleMapHelper?.setOnCameraMovedListener {
            presenter.onMapMovedByUser()
        }
    }
}
