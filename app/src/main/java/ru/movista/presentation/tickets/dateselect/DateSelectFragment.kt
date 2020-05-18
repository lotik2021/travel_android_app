package ru.movista.presentation.tickets.dateselect

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.fragment_date_select.*
import moxy.presenter.InjectPresenter
import org.threeten.bp.LocalDate
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.CustomTypefaceSpan
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.utils.DIVIDER

class DateSelectFragment : BaseFragment(), DateSelectView, OnBackPressedListener {

    companion object {

        fun newInstance(): DateSelectFragment = DateSelectFragment()
    }

    private lateinit var dateSelectCalendar: DateSelectCalendar

    @InjectPresenter
    lateinit var presenter: DateSelectPresenter

    override fun getLayoutRes() = R.layout.fragment_date_select

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(date_select_toolbar)

        date_select_apply_button.setOnClickListener { presenter.onApplyButtonClicked() }

        postOnMainThread {
            dateSelectCalendar = DateSelectCalendar(
                requireContext(),
                resources,
                date_select_calendar_view,
                { presenter.onStartDateSet(it) },
                { presenter.onEndDateSet(it) }
            )
            presenter.onCalendarInitialized()
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun setInitialDates(start: LocalDate?, end: LocalDate?) {
        dateSelectCalendar.setInitialDates(start, end)
    }

    override fun setToolbarText(text: String) {
        selected_dates.text = colorizeToolbarText(text)
    }

    override fun setApplyButtonText(text: String) {
        date_select_apply_button.text = text
    }

    override fun showApplyButton() {
        date_select_apply_button.setVisible()
    }

    override fun hideApplyButton() {
        date_select_apply_button.setGone()
    }

    private fun colorizeToolbarText(text: String): SpannableStringBuilder {

        val fromPart = text.takeWhile { it.toString() != String.DIVIDER }

        val fromDigits = fromPart.filter { it.isDigit() }
        val fromText = fromPart.removePrefix(fromDigits)

        val fromColor = getProperToolbarTextColor(text, resources.getString(R.string.forth))

        val fromDigitsSpannable = createDigitsSpannableString(fromDigits, fromColor)
        val fromTextSpannable = createTextSpannableString(fromText, fromColor)

        val toPart = text.removePrefix(fromPart)

        val toDigits = toPart.filter { it.isDigit() }
        val toText = toPart.filter { !it.isDigit() }

        val toColor = getProperToolbarTextColor(text, resources.getString(R.string.back))

        val toDigitsSpannable = createDigitsSpannableString(toDigits, toColor)
        val toTextSpannable = createTextSpannableString(toText, toColor)

        val toSpannableStringBuilder = SpannableStringBuilder(toTextSpannable)
            .insert(2, toDigitsSpannable)

        return SpannableStringBuilder()
            .append(fromDigitsSpannable)
            .append(fromTextSpannable)
            .append(toSpannableStringBuilder)


    }

    private fun getProperToolbarTextColor(fullText: String, textToBeInactive: String): Int {
        return if (fullText.contains(textToBeInactive, true)) {
            R.color.inactive_text
        } else {
            R.color.blue_primary
        }
    }

    private fun createDigitsSpannableString(text: String, color: Int): SpannableString {
        return SpannableString(text).apply {
            setSpan(
                RelativeSizeSpan(CustomTypefaceSpan.RELATIVE_DIGITS_PROPORTION),
                0,
                this.length,
                0
            )
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), color)),
                0,
                this.length,
                0
            )
            setSpan(
                CustomTypefaceSpan(ResourcesCompat.getFont(requireContext(), R.font.roboto_black)),
                0,
                this.length,
                0
            )
        }
    }

    private fun createTextSpannableString(text: String, color: Int): SpannableString {
        return SpannableString(text).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), color)),
                0,
                this.length,
                0
            )
        }
    }


}


