package ru.movista.presentation.tickets.dateselect

import moxy.MvpView
import org.threeten.bp.LocalDate

interface DateSelectView : MvpView {
    fun setInitialDates(start: LocalDate?, end: LocalDate?)
    fun setToolbarText(text: String)
    fun setApplyButtonText(text: String)
    fun showApplyButton()
    fun hideApplyButton()
}