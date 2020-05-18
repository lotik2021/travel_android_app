package ru.movista.presentation.tickets.dateselect

import android.content.Context
import android.content.res.Resources
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import kotlinx.android.synthetic.main.calendar_month_header.view.*
import org.jetbrains.anko.textColor
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import ru.movista.R
import ru.movista.presentation.custom.CustomTypefaceSpan
import java.util.*

class DateSelectCalendar(
    private val context: Context,
    private val resources: Resources,
    private val calendarView: CalendarView,
    private val onStartDateSelectedListener: (LocalDate?) -> Unit,
    private val onEndDateSelectedListener: (LocalDate?) -> Unit
) {

    init {
        initCalendarView()
    }

    var startDate: LocalDate? = null

    var endDate: LocalDate? = null

    private val today = LocalDate.now()

    fun setInitialDates(start: LocalDate?, end: LocalDate?) {
        startDate = start
        endDate = end
        calendarView.notifyCalendarChanged()
        if (start != null) calendarView.scrollToMonth(start.yearMonth)
    }

    private fun initCalendarView() {

        calendarView.dayBinder = createDayViewBinder()

        calendarView.monthHeaderBinder = createMonthHeaderBinder()
        calendarView.monthFooterBinder = createMonthFooterBinder()

        val currentMonth = YearMonth.now()
        val lastMonth = currentMonth.plusMonths(12)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        calendarView.setup(currentMonth, lastMonth, firstDayOfWeek)
    }

    private fun createDayViewBinder(): DayBinder<DayViewContainer> {
        return object : DayBinder<DayViewContainer> {

            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day

                val dayText = container.textView
                val currentDate = day.date

                fun setStandartDayColor() {
                    dayText.textColor = ContextCompat.getColor(context, R.color.blue_primary)
                }

                fun setInactiveDayColor() {
                    dayText.textColor = ContextCompat.getColor(context, R.color.calendar_inactive_date)
                }

                fun setFullSelectedDayView() {
                    dayText.textColor = ContextCompat.getColor(context, R.color.white)
                    dayText.background = resources.getDrawable(R.drawable.bg_calendar_selected_day_full, null)
                }

                fun setStartSelectedDayView() {
                    dayText.textColor = ContextCompat.getColor(context, R.color.white)
                    dayText.background = resources.getDrawable(R.drawable.bg_calendar_selected_day_start, null)
                }

                fun setEndSelectedDayView() {
                    dayText.textColor = ContextCompat.getColor(context, R.color.white)
                    dayText.background = resources.getDrawable(R.drawable.bg_calendar_selected_day_end, null)
                }

                fun setSelectedInRangeDayView() {
                    dayText.background = resources.getDrawable(R.drawable.bg_calendar_selected_day_in_range, null)
                }

                fun setSelectedSameStartAndEndBayView() {
                    dayText.textColor = ContextCompat.getColor(context, R.color.white)
                    dayText.background = resources.getDrawable(R.drawable.bg_calendar_selected_day, null)
                }

                fun clearTextBackground() {
                    dayText.background = null
                }

                fun setInitialState() {
                    dayText.text = null
                    dayText.typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
                    setStandartDayColor()
                    clearTextBackground()
                }

                setInitialState()

                if (day.owner == DayOwner.THIS_MONTH) {

                    dayText.text = day.day.toString()

                    if (currentDate.isDayOff()) {
                        dayText.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
                    } else {
                        dayText.typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
                    }

                    when {
                        currentDate.isBefore(today) -> {
                            setInactiveDayColor()
                        }
                        startDate == null -> return
                        currentDate == startDate && endDate == null -> {
                            setFullSelectedDayView()
                        }
                        currentDate == startDate && endDate != null -> {
                            if (startDate == endDate) setSelectedSameStartAndEndBayView() else setStartSelectedDayView()
                        }
                        currentDate == endDate -> {
                            setEndSelectedDayView()
                        }
                        endDate != null && day.date.isAfter(startDate) && day.date.isBefore(endDate) -> {
                            setSelectedInRangeDayView()
                        }
                    }
                }
            }
        }
    }

    private fun createMonthHeaderBinder(): MonthHeaderFooterBinder<MonthHeaderContainer> {
        return object : MonthHeaderFooterBinder<MonthHeaderContainer> {

            override fun create(view: View) = MonthHeaderContainer(view)

            override fun bind(container: MonthHeaderContainer, month: CalendarMonth) {
                val yearText = month.yearMonth.format(DateTimeFormatter.ofPattern("yyyy"))
                val yearSpannable = createYearSpannableString(yearText)

                val monthText = resources.getStringArray(R.array.months).get(month.month - 1).toLowerCase()

                val monthTitle = SpannableStringBuilder().append(monthText)
                    .append(" ")
                    .append(yearSpannable)

                container.monthTitle.text = monthTitle
            }

            private fun createYearSpannableString(text: String): SpannableString {
                return SpannableString(text).apply {
                    setSpan(
                        RelativeSizeSpan(CustomTypefaceSpan.RELATIVE_DIGITS_PROPORTION),
                        0,
                        this.length,
                        0
                    )
                    setSpan(
                        CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.roboto_black)),
                        0,
                        this.length,
                        0
                    )
                }
            }
        }
    }

    private fun createMonthFooterBinder(): MonthHeaderFooterBinder<MonthFooterContainer> {
        return object : MonthHeaderFooterBinder<MonthFooterContainer> {
            override fun create(view: View) = MonthFooterContainer(view)

            override fun bind(container: MonthFooterContainer, month: CalendarMonth) {}
        }
    }

    private fun LocalDate.isDayOff(): Boolean {
        return this.dayOfWeek == DayOfWeek.SATURDAY || this.dayOfWeek == DayOfWeek.SUNDAY
    }

    private inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay

        val textView: TextView = view.calendar_day_text

        init {
            textView.setOnClickListener {
                if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(today))) {
                    val date = day.date
                    when {
                        startDate == null -> {
                            startDate = date
                        }
                        endDate != null -> {
                            startDate = date
                            endDate = null
                        }
                        else -> {
                            if (date.isBefore(startDate)) {
                                endDate = startDate
                                startDate = date
                            } else {
                                endDate = date
                            }
                        }
                    }
                    onStartDateSelectedListener.invoke(startDate)
                    onEndDateSelectedListener.invoke(endDate)

                    calendarView.notifyCalendarChanged()
                }
            }
        }
    }

    private inner class MonthHeaderContainer(view: View) : ViewContainer(view) {
        val monthTitle: TextView = view.calendar_month_title
    }

    private inner class MonthFooterContainer(view: View) : ViewContainer(view)
}