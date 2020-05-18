package ru.movista.presentation.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.movista.R
import ru.movista.presentation.common.mapview.MapFragment
import ru.movista.presentation.intro.IntroFragment
import ru.movista.presentation.intro.pager.SlidePageFragment
import ru.movista.presentation.routedetail.RouteDetailFragment
import ru.movista.presentation.selectplaceonmap.SelectPlaceOnMapFragment
import ru.movista.utils.reportNullFieldError

class SystemBarsDecorator(private val activity: Activity?) {

    fun onFragmentCreateView(fragment: BaseFragment) {
        changeSystemUI(fragment)
    }

    fun setTranslucentStatusBar() {

        activity?.window?.apply {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // делаем прозрачный statusbar и отменяем LIGHT_STATUS_BAR
                setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                )
                var flags = decorView.systemUiVisibility
                flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                decorView.systemUiVisibility = flags
            } else {
                // на версиях ниже M LIGHT_STATUS_BAR недоступен, поэтому просто рисуемся под statusbar
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }

            decorView.requestApplyInsets()
        }
    }


    private fun changeSystemUI(fragment: BaseFragment) {
        // NavigationBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            if (fragment is IntroFragment || fragment is SlidePageFragment) {
                setNavigationBarColor(R.color.blue, false)
            } else {
                setNavigationBarColor(android.R.color.white, true)
            }
        }

        // StatusBar
        if (fragment is RouteDetailFragment || fragment is SelectPlaceOnMapFragment || fragment is MapFragment) {
            setTranslucentStatusBar()
        } else if (fragment !is IntroFragment && fragment !is SlidePageFragment) {
            clearTranslucentStatusBar()
        }
    }

    private fun clearTranslucentStatusBar() {

        activity?.window?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

                var flags = decorView.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                decorView.systemUiVisibility = flags
                statusBarColor = Color.WHITE
            } else {
                var flags = decorView.systemUiVisibility
                flags =
                    flags and View.SYSTEM_UI_FLAG_LAYOUT_STABLE.inv() and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.inv()
                decorView.systemUiVisibility = flags
            }

            decorView.requestApplyInsets()
        }
    }


    @SuppressLint("InlinedApi")
    private fun setNavigationBarColor(@ColorRes color: Int, makeLight: Boolean) {
        activity?.let {
            with(it.window) {
                setFlags(
                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                )
                val colorValue = ContextCompat.getColor(it as Context, color)
                clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                if (makeLight) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                navigationBarColor = colorValue

                decorView.requestApplyInsets()
            }
        } ?: reportNullFieldError("activity")
    }

}