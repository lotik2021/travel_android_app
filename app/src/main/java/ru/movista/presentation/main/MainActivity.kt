package ru.movista.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import com.google.android.gms.location.LocationServices
import moxy.presenter.InjectPresenter
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseActivity
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.chat.ChatFragment
import ru.movista.presentation.common.OnAudioPermissionResultListener
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.common.OnLocationPermissionResultListener
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.placesearch.SearchPlaceFragment
import ru.movista.presentation.routedetail.RouteDetailFragment
import ru.movista.presentation.selectplaceonmap.SelectPlaceOnMapFragment
import ru.movista.presentation.utils.goToCurrentApplicationSettings
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@RuntimePermissions
class MainActivity : BaseActivity(), MainView {

    @Inject
    @field:[Named("AppNavigatorHolder")]
    lateinit var navigatorHolder: NavigatorHolder

    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun getLayoutRes() = R.layout.activity_main

    override fun onActivityInject() {
        Injector.init(Screens.Main.TAG)
        Injector.mainComponent?.inject(this)
    }


    override fun afterCreate(savedInstanceState: Bundle?) {
        super.afterCreate(savedInstanceState)

        if (savedInstanceState == null) presenter.coldStart()
    }

    override fun onResume() {
        super.onResume()
        presenter.onAppResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onAppPause()
    }

    override fun getNavigationHolder() = navigatorHolder

    override fun createNavigator(): Navigator {
        return object : SupportAppNavigator(this, supportFragmentManager, R.id.main_container) {

            override fun setupFragmentTransaction(
                command: Command?,
                currentFragment: androidx.fragment.app.Fragment?,
                nextFragment: androidx.fragment.app.Fragment?,
                fragmentTransaction: androidx.fragment.app.FragmentTransaction?
            ) {

                when {
                    currentFragment is ChatFragment && nextFragment is SelectPlaceOnMapFragment -> {
                        fragmentTransaction?.setCustomAnimations(R.anim.slide_in_bottom_fast, R.anim.fade_out_fast)
                    }
                    currentFragment is SelectPlaceOnMapFragment && nextFragment is SearchPlaceFragment -> {
                        fragmentTransaction?.setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out_fast)
                    }
                    currentFragment is ChatFragment && nextFragment is RouteDetailFragment -> {
                        fragmentTransaction?.setCustomAnimations(R.anim.slide_in_bottom_fast, R.anim.fade_out_fast)
                    }
                    else -> {
                        fragmentTransaction?.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val currentFragment = getCurrentFragment()
        if (keyCode == KeyEvent.KEYCODE_BACK && currentFragment is OnBackPressedListener) {
            currentFragment.onBackPressed()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    fun requestLocationPermission() {
        Timber.i("requesting location permission...")
        getLastKnownLocationWithPermissionCheck()
    }

    fun requestAudioPermission() {
        Timber.i("requesting audio permission...")
        getAudioPermissionWithPermissionCheck()
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getLastKnownLocation() {
        val resultListener = getCurrentOnLocationPermissionResultListener()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                if (it == null) {
                    resultListener?.onLastLocationPermissionDenied()
                } else {
                    resultListener?.onLocationPermissionReceived(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener {
                resultListener?.onLastLocationPermissionDenied()
            }
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionDenied() {
        getCurrentOnLocationPermissionResultListener()?.onLastLocationPermissionDenied()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionNeverAskAgain() {
        showNeverAskAgainLocationDialog()
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    fun getAudioPermission() {
        Timber.i("onAudioPermissionGranted")
        getCurrentAudioPermissionResultListener()?.onAudioPermissionGranted()
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    fun onAudioPermissionDenied() {
        Timber.i("onAudioPermissionDenied")
        getCurrentAudioPermissionResultListener()?.onAudioPermissionDenied()
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    fun onAudioPermissionNeverAskAgain() {
        Timber.i("onAudioPermissionNeverAskAgain")
        showNeverAskAgainAudioPermissionDialog()
    }

    private fun getCurrentFragment(): BaseFragment? {
        return supportFragmentManager.findFragmentById(R.id.main_container) as? BaseFragment
    }

    private fun getCurrentOnLocationPermissionResultListener(): OnLocationPermissionResultListener? {
        val shouldSharePresenterFragment = getCurrentFragment()
        return shouldSharePresenterFragment?.sharePresenter() as? OnLocationPermissionResultListener
    }

    private fun getCurrentAudioPermissionResultListener(): OnAudioPermissionResultListener? {
        val shouldSharePresenterFragment = getCurrentFragment()
        return shouldSharePresenterFragment?.sharePresenter() as? OnAudioPermissionResultListener
    }

    private fun showNeverAskAgainAudioPermissionDialog() {
        val dialog = DefaultAlertDialog(this)
            .setTitle(R.string.access_permission)
            .setMessage(R.string.later_access_audio_permission_description)
            .setPositiveButton(R.string.access_permission) { goToCurrentApplicationSettings() }
            .setNegativeButton(R.string.later) {}
            .build()
        dialog.show()
    }

    private fun showNeverAskAgainLocationDialog() {
        val dialog = DefaultAlertDialog(this)
            .setTitle(R.string.access_permission)
            .setMessage(R.string.later_access_location_permission_description)
            .setCancelable(false)
            .setPositiveButton(R.string.access_permission) {
                getCurrentOnLocationPermissionResultListener()?.onNeverAskLocationClicked()
                goToCurrentApplicationSettings()
            }
            .setNegativeButton(R.string.later) {
                getCurrentOnLocationPermissionResultListener()?.onNeverAskLocationLaterClicked()
            }
            .build()
        dialog.show()
    }
}
