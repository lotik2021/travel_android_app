package ru.movista.presentation.tickets.segments.segmentfilter

import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.fragment_segment_filters.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.jetbrains.anko.textColorResource
import ru.movista.R
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.CustomTypefaceSpan
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.tickets.segments.UserRouteFilter
import ru.movista.presentation.utils.setSmallCaps
import ru.movista.presentation.utils.setVisibility
import ru.movista.presentation.utils.setVisible
import ru.movista.utils.minutesToHHMM

class SegmentFilterFragment : BaseFragment(), SegmentFilterView, OnBackPressedListener {
    companion object {
        private const val ROUTES_KEY = "routes_key"
        private const val USER_ROUTE_FILTER_KEY = "user_route_filter_key"
        private const val SEARCH_PARAMS_KEY = "search_params_key"

        fun newInstance(
            routes: List<Route>,
            userRouteFilter: UserRouteFilter?,
            searchParams: SearchModel
        ): SegmentFilterFragment {
            return SegmentFilterFragment().apply {
                arguments = bundleOf(
                    ROUTES_KEY to routes,
                    USER_ROUTE_FILTER_KEY to userRouteFilter,
                    SEARCH_PARAMS_KEY to searchParams
                )
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: SegmentFilterPresenter

    @ProvidePresenter
    fun providePresenter(): SegmentFilterPresenter {
        return SegmentFilterPresenter(
            arguments?.getParcelableArrayList<Route>(ROUTES_KEY) as List<Route>,
            arguments?.getParcelable(USER_ROUTE_FILTER_KEY) as UserRouteFilter?,
            arguments?.getParcelable(SEARCH_PARAMS_KEY) as SearchModel
        )
    }

    override fun getLayoutRes() = R.layout.fragment_segment_filters

    override fun initUI() {
        super.initUI()
        setupToolbarBackButton(filters_toolbar)

        filter_max_transfer_count_range_bar.setDrawTicks(true)

        filters_carriers_button.setOnClickListener { presenter.onCarriersFilterClick() }
        filters_departure_points_button.setOnClickListener { presenter.onDeparturePointsFilterClick() }
        filters_arrival_points_button.setOnClickListener { presenter.onArrivalPointsFilterClick() }

        reset_time.setOnClickListener { presenter.onClearTimeClick() }
        reset_route.setOnClickListener { presenter.onClearRouteClick() }
        reset_points.setOnClickListener { presenter.onClearPointsClick() }
        reset_all.setOnClickListener { presenter.omResetAllClick() }
        apply.setOnClickListener { presenter.onApplyClick() }
    }


    override fun setCarriersLabelText(text: String) {
        filters_carriers_button_label.text = text
    }

    override fun setCarriersLabelEnabled(enabled: Boolean) {
        if (enabled) {
            filters_carriers_button_label.setTextColor(getColor(requireContext(), R.color.white))
            filters_carriers_button_label.backgroundTintList = getColorStateList(
                requireContext(), R.color.light_blue
            )
        } else {
            filters_carriers_button_label.setTextColor(getColor(requireContext(), R.color.turbid_blue))
            filters_carriers_button_label.backgroundTintList = getColorStateList(
                requireContext(), R.color.blue_light
            )
        }
    }

    override fun setDeparturePointsLabelText(text: String) {
        filters_departure_points_button_label.text = text
    }

    override fun setDeparturePointsLabelEnabled(enabled: Boolean) {
        if (enabled) {
            filters_departure_points_button_label.setTextColor(getColor(requireContext(), R.color.white))
            filters_departure_points_button_label.backgroundTintList = getColorStateList(
                requireContext(), R.color.light_blue
            )
        } else {
            filters_departure_points_button_label.setTextColor(getColor(requireContext(), R.color.turbid_blue))
            filters_departure_points_button_label.backgroundTintList = getColorStateList(
                requireContext(), R.color.blue_light
            )
        }
    }

    override fun setArrivalPointsLabelText(text: String) {
        filters_arrival_points_button_label.text = text
    }

    override fun setArrivalPointsLabelEnabled(enabled: Boolean) {
        if (enabled) {
            filters_arrival_points_button_label.setTextColor(getColor(requireContext(), R.color.white))
            filters_arrival_points_button_label.backgroundTintList = getColorStateList(
                requireContext(), R.color.light_blue
            )
        } else {
            filters_arrival_points_button_label.setTextColor(getColor(requireContext(), R.color.turbid_blue))
            filters_arrival_points_button_label.backgroundTintList = getColorStateList(
                requireContext(), R.color.blue_light
            )
        }
    }


    override fun setResetTimeBtnVisibility(visible: Boolean) {
        reset_time.setVisibility(visible)
    }

    override fun setResetRouteBtnVisibility(visible: Boolean) {
        reset_route.setVisibility(visible)
    }

    override fun setResetPointsBtnVisibility(visible: Boolean) {
        reset_points.setVisibility(visible)
    }

    override fun setAllResetBtnsVisibility(visible: Boolean) {
        reset_all.setVisibility(visible)
    }

    override fun disableMaxTripDurationRangeBar() {
        max_trip_duration_range_bar.setOnRangeBarListener(null)
        max_trip_duration_range_bar.isEnabled = false
    }

    override fun disablePriceRangeBar() {
        filter_price_range_bar.setOnRangeBarListener(null)
        filter_price_range_bar.isEnabled = false
    }

    override fun disableMaxTransferCountRangeBar() {
        filter_max_transfer_count_range_bar.setOnRangeBarListener(null)
        filter_max_transfer_count_range_bar.isEnabled = false
    }

    override fun setFiltersCarriersLabel(text: String?) {
        with(filters_carriers_button_label) {
            if (text == null) {
                this.text = getString(R.string.all)
                backgroundTintList = ColorStateList.valueOf(getColor(requireContext(), R.color.blue_light))
                setTextColor(getColor(requireContext(), R.color.turbid_blue))
            } else {
                this.text = text
                backgroundTintList = ColorStateList.valueOf(getColor(requireContext(), R.color.blue_primary))
                setTextColor(getColor(requireContext(), R.color.white))
            }
        }
    }

    override fun setFiltersDeparturePointsLabel(text: String?) {
        with(filters_departure_points_button_label) {
            if (text == null) {
                this.text = getString(R.string.all)
                backgroundTintList = ColorStateList.valueOf(getColor(requireContext(), R.color.blue_light))
                setTextColor(getColor(requireContext(), R.color.turbid_blue))
            } else {
                this.text = text
                backgroundTintList = ColorStateList.valueOf(getColor(requireContext(), R.color.blue_primary))
                setTextColor(getColor(requireContext(), R.color.white))
            }
        }
    }

    override fun setFiltersArrivalPointsLabel(text: String?) {
        with(filters_arrival_points_button_label) {
            if (text == null) {
                this.text = getString(R.string.all)
                backgroundTintList = ColorStateList.valueOf(getColor(requireContext(), R.color.blue_light))
                setTextColor(getColor(requireContext(), R.color.turbid_blue))
            } else {
                this.text = text
                backgroundTintList = ColorStateList.valueOf(getColor(requireContext(), R.color.blue_primary))
                setTextColor(getColor(requireContext(), R.color.white))
            }
        }
    }

    override fun setTripDurationLabels(min: String, max: String) {
        filters_min_trip_duration_label.setSmallCaps(min)
        filters_max_trip_duration_label.setSmallCaps(max)
    }

    override fun setDepartureRangeBarIndex(minTime: Long, maxTime: Long) {
        filter_departure_range_bar.setRangePinsByIndices(minTime.toInt(), maxTime.toInt())
    }

    override fun setupDepartureRangeBar(min: Long, max: Long) {
        filter_departure_range_bar.tickEnd = max.toFloat()
        filter_departure_range_bar.tickStart = min.toFloat()
    }

    override fun setArrivalRangeBarIndex(minTime: Long, maxTime: Long) {
        filter_arrival_range_bar.setRangePinsByIndices(minTime.toInt(), maxTime.toInt())
    }

    override fun setupArrivalRangeBar(min: Long, max: Long) {
        filter_arrival_range_bar.tickEnd = max.toFloat()
        filter_arrival_range_bar.tickStart = min.toFloat()
    }

    override fun setupMaxTripDurationRangeBar(min: Long, max: Long) {
        max_trip_duration_range_bar.tickEnd = max.toFloat()
        max_trip_duration_range_bar.tickStart = min.toFloat()
    }

    override fun setMaxTripDurationRangeBarIndex(maxTime: Long) {
        max_trip_duration_range_bar.setSeekPinByIndex(maxTime.toInt())
    }

    override fun setupPriceRangeBar(min: Int, max: Int) {
        filter_price_range_bar.tickEnd = max.toFloat()
        filter_price_range_bar.tickStart = min.toFloat()
    }

    override fun setPriceRangeBarIndex(minPrice: Int, maxPrice: Int) {
        filter_price_range_bar.setRangePinsByIndices(minPrice, maxPrice)
    }

    override fun setMaxTransferCountRangeBarIndex(maxCount: Int) {
        filter_max_transfer_count_range_bar.setSeekPinByIndex(maxCount)
    }

    override fun setupMaxTransferCountRangeBar(maxTransferCount: Int) {
        filter_max_transfer_count_range_bar.tickEnd = maxTransferCount.toFloat()
        filter_max_transfer_count_range_bar.tickStart = 0F
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun setApplyBtnEnabled(enabled: Boolean) {
        apply.isEnabled = enabled
    }

    override fun setApplyBtnText(secondaryText: String) {
        val title = getString(R.string.show)
        val builder = SpannableStringBuilder("$title\n")

        val spannable = SpannableString(secondaryText).apply {
            setSpan(RelativeSizeSpan(0.75F), 0, this.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(
                ForegroundColorSpan(
                    getColor(
                        requireContext(),
                        R.color.transparent_white
                    )
                ), 0, this.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                CustomTypefaceSpan(
                    ResourcesCompat.getFont(
                        requireContext(),
                        R.font.roboto_regular
                    )
                ), 0, this.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        builder.append(spannable)
        apply.text = builder
        apply.setVisible()
    }

    override fun setMaxTripDurationLabel(text: String) {
        max_trip_duration_label.text = getString(R.string.trip_max_time, text)
    }

    override fun setMaxTripDurationLabelColor(@ColorRes colorResId: Int) {
        filters_max_trip_duration_label.textColorResource = colorResId
    }

    override fun setMaxTransferCountLabel(text: String) {
        filter_max_transfer_count_label.text = text
    }

    override fun enableRangeBarsListeners() {
        filter_departure_range_bar.setOnRangeBarListener { leftIndex, rightIndex ->
            presenter.onDepartureRangeChanged(leftIndex, rightIndex)
        }

        filter_arrival_range_bar.setOnRangeBarListener { leftIndex, rightIndex ->
            presenter.onArrivalRangeChanged(leftIndex, rightIndex)
        }

        max_trip_duration_range_bar.setOnRangeBarListener { _, rightIndex ->
            presenter.onMaxTripDurationChanged(rightIndex)
        }

        filter_price_range_bar.setOnRangeBarListener { leftIndex, rightIndex ->
            presenter.onPriceRangeChanged(leftIndex, rightIndex)
        }

        filter_max_transfer_count_range_bar.setOnRangeBarListener { _, rightIndex ->
            presenter.onMaxTransferCountChanged(rightIndex)
        }

        filter_departure_range_bar.setPinTextListener { _, tickIndex -> minutesToHHMM(tickIndex) }
        filter_arrival_range_bar.setPinTextListener { _, tickIndex -> minutesToHHMM(tickIndex) }

        max_trip_duration_range_bar.setPinTextListener { _, _ -> "" }

        filter_price_range_bar.setPinTextListener { _, tickIndex ->
            presenter.getFormattedPrice(tickIndex)
        }

        filter_max_transfer_count_range_bar.setPinTextListener { _, tickIndex ->
            presenter.getFormattedTransferCount(tickIndex)
        }
    }

    override fun showTicketsExpiredTimer() {
        val dialog = DefaultAlertDialog(requireContext())
            .setCancelable(false)
            .setTitle(R.string.tickets_expired_alert_title)
            .setMessage(R.string.tickets_expired_alert_description)
            .setPositiveButton(R.string.yes) { presenter.onRepeatExpiredSearchClick() }
            .setNegativeButton(R.string.no) { presenter.onCancelSearchClick() }
            .build()
        dialog.show()
    }
}
