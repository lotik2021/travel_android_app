package ru.movista.presentation.tickets.segments

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.empty_search_result_item.*
import kotlinx.android.synthetic.main.fragment_segments_list.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.data.source.local.models.TicketSortingOption
import ru.movista.domain.model.tickets.Route
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.custom.MarginDecoration
import ru.movista.presentation.tickets.segments.sorting.SortingOptionsDialog
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setSmallCaps
import ru.movista.presentation.utils.setVisibility
import ru.movista.presentation.utils.setVisible

class SegmentsFragment : BaseFragment(), SegmentsView, OnBackPressedListener {

    companion object {
        private const val SEGMENT_ARGUMENTS_KEY = "segment_arguments_key"

        fun newInstance(segmentArguments: SegmentArgumentsWrapper): SegmentsFragment {
            return SegmentsFragment().apply {
                arguments = bundleOf(SEGMENT_ARGUMENTS_KEY to segmentArguments)
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: SegmentsPresenter
    private lateinit var adapter: SegmentsAdapter

    @ProvidePresenter
    fun providePresenter(): SegmentsPresenter {
        return SegmentsPresenter(
            arguments?.getParcelable(SEGMENT_ARGUMENTS_KEY) as SegmentArgumentsWrapper
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = SegmentsAdapter { presenter.onRouteClick(it) }
    }

    override fun getLayoutRes() = R.layout.fragment_segments_list

    override fun initUI() {
        super.initUI()
        setupToolbarBackButton(segments_list_toolbar)

        search_result_title.text = getString(R.string.segment_routes_not_found_title)
        search_result_description.text = getString(R.string.segment_routes_not_found_description)
        new_search.text = getString(R.string.just_back)

        new_search.setOnClickListener { presenter.onEmptyRoutesBtnClick() }
        filter.setOnClickListener { presenter.onFilterClick() }
        sort.setOnClickListener { presenter.onSortClick() }

        segments_list_recycler_view.layoutManager = LinearLayoutManager(context)
        segments_list_recycler_view.adapter = adapter
        segments_list_recycler_view.addItemDecoration(
            MarginDecoration(
                context = requireContext(),
                topMarginRes = R.dimen.segments_list_margin_top,
                bottomMarginRes = R.dimen.segments_list_margin_bottom
            )
        )
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun openSortingOptions(ticketSortingOption: TicketSortingOption) {
        val dialogFragment = SortingOptionsDialog.newInstance(ticketSortingOption)
        dialogFragment.show(childFragmentManager, SortingOptionsDialog::class.java.name)
    }

    override fun setFiltersVisibility(visible: Boolean) {
        filter.setVisibility(visible)
    }

    override fun setTitle(description: String) {
        segments_list_title.setSmallCaps(description)
    }

    override fun updateTickets(routes: List<Route>) {
        adapter.updateItems(routes)
        segments_list_recycler_view.scrollToPosition(0)
    }

    override fun clearUI() {
        super.clearUI()
        segments_list_recycler_view.adapter = null
    }

    override fun showEmptyRoutesMessage() {
        segments_list_recycler_view.setGone()
        empty_search_result_root.setVisible()
    }

    override fun setEnabledFilterIndicator(enabled: Boolean) {
        if (enabled) {
            filter.setImageResource(R.drawable.ic_activated_filter)
            filter_indicator.setVisible()
        } else {
            filter.setImageResource(R.drawable.ic_filter)
            filter_indicator.setGone()
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