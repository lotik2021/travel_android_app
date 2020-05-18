package ru.movista.presentation.tickets.searchtickets

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getFont
import kotlinx.android.synthetic.main.fragment_search_tickets.*
import moxy.presenter.InjectPresenter
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textColorResource
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.CustomTypefaceSpan
import ru.movista.presentation.tickets.comforttypeselect.SelectComfortTypeDialogFragment
import ru.movista.presentation.utils.postOnMainThread

class SearchTicketsFragment : BaseFragment(), SearchTicketsView, OnBackPressedListener {

    companion object {
        private const val TAG_SELECT_COMFORT_TYPE = "select_comfort_type"

        fun newInstance(): SearchTicketsFragment =
            SearchTicketsFragment()
    }

    @InjectPresenter
    lateinit var presenter: SearchTicketsPresenter

    override fun getLayoutRes() = R.layout.fragment_search_tickets

    override fun initUI() {
        super.initUI()
        search_tickets_from_cv.setOnClickListener { presenter.onFromClicked() }
        search_tickets_to_cv.setOnClickListener { presenter.onToClicked() }
        search_tickets_from_date_cv.setOnClickListener { presenter.onFromDateClicked() }
        search_tickets_to_date_cv.setOnClickListener { presenter.onToDateClicked() }
        search_tickets_passengers_cv.setOnClickListener { presenter.onPassengersClicked() }
        search_tickets_comfort_type_cv.setOnClickListener { presenter.onComfortTypeClicked() }
        search_tickets_swap_cities_button.setOnClickListener { presenter.onSwapPlacesClicked() }
        search_tickets_search_button.setOnClickListener { presenter.onSearchClicked() }
        search_tickets_toolbar.setNavigationOnClickListener { presenter.onChatScreenClick() }
    }


    override fun setFromPlace(name: String, lookInactive: Boolean) {
        search_tickets_from_name.text = name
        search_tickets_from_name.textColorResource = if (lookInactive) {
            R.color.inactive_text
        } else {
            R.color.blue_primary
        }
    }

    override fun setToPlace(name: String, lookInactive: Boolean) {
        search_tickets_to_city.text = name
        search_tickets_to_city.textColorResource = if (lookInactive) {
            R.color.inactive_text
        } else {
            R.color.blue_primary
        }
    }

    override fun setFromDate(date: String, lookInactive: Boolean) {
        search_tickets_from_date.text = createDateSpannable(date)

        if (lookInactive) {
            search_tickets_from_date.textColor = getColor(requireContext(), R.color.text_blue_light)
            search_tickets_from_date.typeface = getFont(requireContext(), R.font.roboto_medium)
        } else {
            search_tickets_from_date.textColor = getColor(requireContext(), R.color.blue_primary)
            search_tickets_from_date.typeface = getFont(requireContext(), R.font.roboto_bold)
        }
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun setToDate(date: String, lookInactive: Boolean) {
        search_tickets_to_date.text = createDateSpannable(date)
        if (lookInactive) {
            search_tickets_to_date.textColor = getColor(requireContext(), R.color.text_blue_light)
            search_tickets_to_date.typeface = getFont(requireContext(), R.font.roboto_medium)
        } else {
            search_tickets_to_date.textColor = getColor(requireContext(), R.color.blue_primary)
            search_tickets_to_date.typeface = getFont(requireContext(), R.font.roboto_bold)
        }
    }

    override fun setPassengersInfo(adultsCount: String, childrenCount: String) {
        search_tickets_adult_count.text = adultsCount
        search_tickets_children_count.text = childrenCount
    }

    override fun showSelectComfortTypeDialog(comfortTypeValue: String) {
        postOnMainThread {
            val selectComfortTypeDialogFragment =
                SelectComfortTypeDialogFragment.newInstance(comfortTypeValue)
            selectComfortTypeDialogFragment.show(childFragmentManager, TAG_SELECT_COMFORT_TYPE)
        }
    }

    override fun setComfortType(comfortType: String) {
        search_tickets_comfort_type.text = comfortType
    }

    override fun showError(@StringRes errorMsgRes: Int) {
        showMessage(search_ticket_root, errorMsgRes)
    }

    private fun createDateSpannable(date: String): SpannableStringBuilder {

        val dateDigits = date.filter { it.isDigit() }

        val digitsSpannableString = createDigitsRelativeSpannable(dateDigits)

        val dateLetters = date.filter { !it.isDigit() }

        return SpannableStringBuilder().append(digitsSpannableString, dateLetters)
    }

    private fun createDigitsRelativeSpannable(digits: String): SpannableString {
        return SpannableString(digits).apply {
            setSpan(
                RelativeSizeSpan(CustomTypefaceSpan.RELATIVE_DIGITS_PROPORTION),
                0,
                this.length,
                0
            )

            setSpan(
                CustomTypefaceSpan(getFont(requireContext(), R.font.roboto_black)),
                0,
                this.length,
                0
            )
        }
    }
}


