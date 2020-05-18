package ru.movista.presentation.tickets.dateselect

import android.content.res.Resources
import moxy.InjectViewState
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.domain.usecase.tickets.searchparams.IDateSelectUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class DateSelectPresenter : BasePresenter<DateSelectView>() {

    companion object {
        private const val DAY_MONTH_PATTERN = "d MMMM"
    }

    override val screenTag: String
        get() = Screens.DateSelect.TAG

    private var departureDate: LocalDate? = null
    private var arrivalDate: LocalDate? = null

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var dateSelectUseCase: IDateSelectUseCase

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.dateSelectComponent?.inject(this)
    }

    fun onBackPressed() {
        router.exit()
    }

    fun onCalendarInitialized() {
        setupInitialDates()

        setToolbarText()

        if (departureDate != null) {
            viewState.showApplyButton()
            setApplyButtonText()
        } else {
            viewState.hideApplyButton()
        }
    }

    fun onStartDateSet(date: LocalDate?) {
        departureDate = date
        setToolbarText()
        if (date != null) {
            viewState.showApplyButton()
            setApplyButtonText()
        }
    }

    fun onEndDateSet(date: LocalDate?) {
        arrivalDate = date
        setToolbarText()
        setApplyButtonText()
    }

    fun onApplyButtonClicked() {
        Timber.i("Departure date: $departureDate, arrival date: $arrivalDate")

        departureDate?.let {
            dateSelectUseCase.setTripDate(it, arrivalDate)
            router.exit()
        } ?: Timber.e(IllegalStateException("departureDate must not be null"))
    }

    private fun setupInitialDates() {
        val initialDates = dateSelectUseCase.getInitialDates()
        departureDate = initialDates.departureDate
        arrivalDate = initialDates.arrivalDate
        viewState.setInitialDates(departureDate, arrivalDate)
    }

    private fun setToolbarText() {
        val departureText = departureDate?.format(DateTimeFormatter.ofPattern(DAY_MONTH_PATTERN))?.toLowerCase()
            ?: resources.getString(R.string.forth).toLowerCase()
        val arrivalText = arrivalDate?.format(DateTimeFormatter.ofPattern(DAY_MONTH_PATTERN))?.toLowerCase()
            ?: resources.getString(R.string.back).toLowerCase()

        val text = resources.getString(R.string.departure_date_to_arrival_date, departureText, arrivalText)
        viewState.setToolbarText(text)
    }

    private fun setApplyButtonText() {
        val text = if (arrivalDate != null)
            resources.getString(R.string.done)
        else resources.getString(R.string.skip_arrival)

        viewState.setApplyButtonText(text)
    }
}