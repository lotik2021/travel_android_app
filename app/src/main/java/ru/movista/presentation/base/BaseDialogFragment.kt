package ru.movista.presentation.base

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.movista.R
import ru.movista.di.Injector

abstract class BaseDialogFragment : BottomSheetDialogFragment() {
    val analyticsManager = Injector.mainComponent?.getAnalyticsManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analyticsManager?.reportChangeCurrentScreenFromFragment(activity, this)
    }

    override fun getTheme() = R.style.BottomSheetDialogTheme
}