package ru.movista.presentation.tickets.tripgroup

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.empty_search_result_item.*
import kotlinx.android.synthetic.main.fragment_trip_group.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.model.tickets.TripGroup
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.custom.MarginDecoration
import ru.movista.presentation.utils.*
import ru.movista.utils.toDefaultLowerCase

class TripGroupFragment : BaseFragment(), TripGroupView, OnBackPressedListener {
    companion object {
        private const val SEARCH_PARAM_KEY = "search_param_key"

        fun newInstance(searchParams: SearchModel): TripGroupFragment {
            return TripGroupFragment().apply {
                arguments = bundleOf(SEARCH_PARAM_KEY to searchParams)
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: TripGroupPresenter

    private lateinit var adapter: TripGroupAdapter
    private var snackBar: Snackbar? = null

    @ProvidePresenter
    fun providePresenter(): TripGroupPresenter {
        return TripGroupPresenter(arguments?.getParcelable(SEARCH_PARAM_KEY) as SearchModel)
    }

    override fun getLayoutRes() = R.layout.fragment_trip_group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TripGroupAdapter(presenter.isShowSearchHint, searchResultActions)
    }

    override fun initUI() {
        super.initUI()
        setupToolbarBackButton(ticket_search_toolbar)

        new_search.setOnClickListener { presenter.onNewSearchClick() }
        search_result_recycler_view.layoutManager = LinearLayoutManager(context)
        search_result_recycler_view.adapter = adapter
        search_result_recycler_view.addItemDecoration(
            MarginDecoration(
                context = requireContext(),
                topMarginRes = R.dimen.segments_list_margin_top
            )
        )
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun showErrorMsg(message: String, action: View.OnClickListener) {
        postOnMainThreadWithDelay(200) {
            snackBar = search_ticket_result_root.infiniteSnackBar(
                message,
                getString(R.string.update),
                action
            )
        }
    }

    override fun hideErrorMsg() {
        snackBar?.dismiss()
    }

    override fun showEmptyRoutesMessage() {
        search_result_recycler_view.setGone()
        empty_search_result_root.setVisible()
    }

    override fun setLoaderVisibility(visible: Boolean) {
        if (visible) toolbar_shadow.setInVisible() else toolbar_shadow.setVisible()
        group_toolbar_loader.setVisibility(visible)
    }

    override fun updateSearchResultItems(result: List<TripGroup>, completedSearch: Boolean) {
        adapter.updateItems(result, completedSearch)
    }

    override fun restartSearchResultUi() {
        adapter.restartAdapter()
        search_result_recycler_view.setVisible()
        empty_search_result_root.setGone()
    }

    override fun setOnlyLoaderVisibility(visible: Boolean) {
        if (visible) {
            group_content.setGone()
            toolbar_shadow.setInVisible()
            group_search_loader.setVisible()
            group_toolbar_loader.setVisible()
        } else {
            group_content.setVisible()
            toolbar_shadow.setVisible()
            group_search_loader.setGone()
            group_toolbar_loader.setGone()
        }
    }

    override fun setTripPlace(fromPlace: String?, toPlace: String?) {
        trip_place.setSmallCaps(getString(R.string.dash_split_text, fromPlace, toPlace))
    }

    override fun setDate(toDate: String, returnDate: String?) {
        val resultDate = if (returnDate != null) {
            getString(R.string.dash_split_text, toDate, returnDate)
        } else {
            toDate
        }

        date.text = resultDate.toDefaultLowerCase().replace(".", "")
    }

    override fun setComfortType(comfortType: String) {
        comfort_type.text = comfortType
    }

    override fun removeSearchHint() {
        adapter.removeSearchHint()
    }

    override fun clearUI() {
        super.clearUI()
        search_result_recycler_view.adapter = null
        snackBar?.dismiss()
        snackBar = null
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

    private val searchResultActions = object : TripGroupAdapter.SearchResultActions {
        override fun onGroupClick(tripGroup: TripGroup) {
            presenter.onTicketClick(tripGroup)
        }

        override fun onNotShowSearchHintClick() {
            presenter.onNotShowSearchHintClick()
        }
    }
}