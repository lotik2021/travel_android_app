package ru.movista.presentation.tickets.segments.providersfilter

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.fragment_route_place_filter.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.data.source.local.models.RouteFilterType
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.custom.MarginDecoration
import ru.movista.presentation.tickets.segments.RoutePlaceFilter
import ru.movista.presentation.utils.setVisibility
import ru.movista.utils.toDefaultLowerCase
import java.util.*

class RoutePlaceFilterFragment: BaseFragment(), RoutePlaceFilterView, OnBackPressedListener {
    companion object {
        private const val ROUTE_PLACE_FILTERS_KEY = "route_place_filters_key"
        private const val FILTER_TYPE_KEY = "filter_type_key"
        private const val SEARCH_PARAMS_KEY = "search_params_key"

        fun newInstance(
            filterType: RouteFilterType,
            routePlaceFilters: List<RoutePlaceFilter>,
            searchParams: SearchModel
        ): RoutePlaceFilterFragment {
            return RoutePlaceFilterFragment().apply {
                arguments = bundleOf(
                    FILTER_TYPE_KEY to filterType,
                    ROUTE_PLACE_FILTERS_KEY to routePlaceFilters,
                    SEARCH_PARAMS_KEY to searchParams
                )
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: RoutePlaceFilterPresenter
    lateinit var adapter: RoutePlaceFilterAdapter

    @ProvidePresenter
    fun providePresenter(): RoutePlaceFilterPresenter {
        return RoutePlaceFilterPresenter(
            arguments?.getParcelable(FILTER_TYPE_KEY) as RouteFilterType,
            arguments?.getParcelableArrayList<RoutePlaceFilter>(ROUTE_PLACE_FILTERS_KEY) as ArrayList<RoutePlaceFilter>,
            arguments?.getParcelable(SEARCH_PARAMS_KEY) as SearchModel
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = RoutePlaceFilterAdapter {
            presenter.onFilterItemChanged()
        }
    }

    override fun getLayoutRes() = R.layout.fragment_route_place_filter

    override fun initUI() {
        super.initUI()
        setupToolbarBackButton(providers_toolbar)

        apply.setOnClickListener { presenter.onApplyClick() }

        place_recycler_view.layoutManager = LinearLayoutManager(context)
        place_recycler_view.adapter = adapter
        (place_recycler_view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        place_recycler_view.addItemDecoration(
            MarginDecoration(
                context = requireContext(),
                bottomMarginRes = R.dimen.segments_list_margin_bottom
            )
        )
    }

    override fun setTitle(titleResId: Int) {
        toolbar_title.text = getString(titleResId).toDefaultLowerCase()
    }

    override fun updateFilter(filtersItem: List<BaseFilterViewModel>) {
        adapter.update(filtersItem)
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun setApplyBtnVisibility(visible: Boolean) {
        apply.setVisibility(visible)
    }

    override fun clearUI() {
        super.clearUI()
        place_recycler_view.adapter = null
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
