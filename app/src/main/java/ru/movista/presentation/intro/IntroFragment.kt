package ru.movista.presentation.intro

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_intro.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.intro.pager.ImagePagerIndicator
import ru.movista.presentation.intro.pager.SlidePageFragment
import ru.movista.presentation.intro.pager.SlidePageType
import ru.movista.presentation.utils.SimplePageChangeListener
import ru.movista.presentation.utils.goToCurrentApplicationSettings
import ru.movista.presentation.utils.setInVisible
import ru.movista.utils.reportNullFieldError

class IntroFragment : BaseFragment(), IntroView, OnBackPressedListener {
    companion object {
        private const val GEO_PERMISSIONS_REQUEST_CODE = 1

        fun newInstance() = IntroFragment()
    }

    @InjectPresenter
    lateinit var presenter: IntroPresenter
    private lateinit var imagePagerIndicator: ImagePagerIndicator
    private var dialog: AlertDialog? = null

    /*
     * Размер текста в макетах указан в dp,
     * чтобы он не изменился при увеличении размера шрифта из настроек Android'а
     */
    override fun getLayoutRes(): Int = R.layout.fragment_intro

    override fun initUI() {
        super.initUI()
        content.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        initViewPager()
        next.setOnClickListener { presenter.nextClick() }
    }

    override fun afterCreate() {
        super.afterCreate()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun setCurrentPage(page: Int) {
        pager.currentItem = page
    }

    override fun hidePagerIndicator() {
        pager_indicator.setInVisible()
    }

    override fun hideNextButton() {
        next.isEnabled = false
        next.setInVisible()
    }

    override fun disableNextButton() {
        next.isEnabled = false
    }

    override fun disablePaging() {
        pager.setPagingEnabled(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pager.removeOnPageChangeListener(imagePagerIndicator)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == GEO_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onGrantedLocationPermissions()
                } else if (!shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) ||
                    !shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)
                ) {
                    presenter.onNeverRequestLocationPermissions()
                } else {
                    presenter.onDeniedLocationPermissions()
                }
            }
        }
    }

    override fun requestGeoPermissions() {
        context?.let { context ->
            if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), GEO_PERMISSIONS_REQUEST_CODE)
            } else {
                presenter.onGrantedLocationPermissions()
            }
        } ?: reportNullFieldError("context")
    }

    override fun showLaterAccessPermissionDialog() {
        context?.let { context ->
            dialog = DefaultAlertDialog(context)
                .setTitle(R.string.access_permission)
                .setMessage(R.string.later_access_location_permission_description)
                .setPositiveButton(R.string.access_permission) { presenter.onAccessPermissionClick() }
                .setNegativeButton(R.string.later) { presenter.onLaterAccessPermissionClick() }
                .setOnDismissListener { presenter.onDismissAlertDialog() }
                .build()
            dialog?.show()
        } ?: reportNullFieldError("context")
    }

    override fun hideAlertDialog() {
        dialog?.cancel()
        dialog = null
    }

    override fun goToApplicationSettings() {
        requireContext().goToCurrentApplicationSettings()
    }

    private fun initViewPager() {
        imagePagerIndicator = ImagePagerIndicator(pager_indicator, SlidePageType.values().size)
        pager.addOnPageChangeListener(imagePagerIndicator)

        pager.addOnPageChangeListener(object : SimplePageChangeListener() {
            override fun onPageSelected(position: Int) {
                presenter.onPageChangeListener(position)
                reportChangeScreenName(position)
            }
        })

        pager.adapter = SlidePagerAdapter(childFragmentManager)
    }

    private fun reportChangeScreenName(position: Int) {
        when (position) {
            SlidePageType.SLIDE_PAGE_FIVE.page -> {
                analyticsManager?.reportChangeCurrentScreenName(activity, "onboarding_legal")
            }
            SlidePageType.SLIDE_PAGE_SIX.page -> {
                analyticsManager?.reportChangeCurrentScreenName(activity, "onboarding_location")
            }
        }
    }

    private class SlidePagerAdapter(fm: androidx.fragment.app.FragmentManager) :
        androidx.fragment.app.FragmentPagerAdapter(fm) {
        override fun getCount(): Int = SlidePageType.values().size

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return SlidePageFragment.newInstance(SlidePageType.values()[position])
        }
    }
}