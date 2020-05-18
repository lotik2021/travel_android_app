package ru.movista.presentation.tickets.detailroute

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_detail_routes.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.domain.model.tickets.Alternative
import ru.movista.domain.model.tickets.Route
import ru.movista.domain.model.tickets.TripPlace
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.CustomTypefaceSpan
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.custom.HorizontalDivider
import ru.movista.presentation.custom.MarginDecoration
import ru.movista.presentation.tickets.segments.SegmentArgumentsWrapper
import ru.movista.presentation.utils.*

class DetailRoutesFragment : BaseFragment(), DetailRouteView, OnBackPressedListener {
    companion object {
        private const val SEGMENT_ARGUMENTS_KEY = "segment_arguments_key"
        private const val DETAIL_ROUTES_MODE_KEY = "detail_routes_mode_key"

        fun newInstance(
            segmentArguments: SegmentArgumentsWrapper,
            detailRoutesMode: DetailRoutesMode
        ): DetailRoutesFragment {
            return DetailRoutesFragment().apply {
                arguments = bundleOf(
                    SEGMENT_ARGUMENTS_KEY to segmentArguments,
                    DETAIL_ROUTES_MODE_KEY to detailRoutesMode
                )
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: DetailRoutesPresenter
    private lateinit var adapter: DetailRoutesAdapter
    private var dialog: AlertDialog? = null

    @ProvidePresenter
    fun providePresenter(): DetailRoutesPresenter {
        return DetailRoutesPresenter(
            arguments?.getParcelable(SEGMENT_ARGUMENTS_KEY) as SegmentArgumentsWrapper,
            arguments?.getParcelable(DETAIL_ROUTES_MODE_KEY) as DetailRoutesMode
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = DetailRoutesAdapter(object : DetailRoutesAdapter.Actions {
            override fun onSelectedAlternative(alternative: Alternative) {
                presenter.onSelectedAlternative(alternative)
            }

            override fun onPriceInfoClick() {
                presenter.onPriceInfoClick()
            }
        })
    }

    override fun getLayoutRes() = R.layout.fragment_detail_routes

    override fun initUI() {
        super.initUI()
        arguments?.clear() // todo приходится очищать bundle, так как крашиться из-за нехватики памяти - перетащить эти модели в другой класс
        setupToolbarBackButton(detail_routes_toolbar)

        detail_routes_recycler_view.layoutManager = LinearLayoutManager(context)
        detail_routes_recycler_view.adapter = adapter
        detail_routes_recycler_view.addItemDecoration(
            HorizontalDivider(
                context = requireContext(),
                leftMarginRes = R.dimen.detail_route_divider_margin
            )
        )
        detail_routes_recycler_view.addItemDecoration(
            MarginDecoration(
                context = requireContext(),
                bottomMarginRes = R.dimen.segments_list_margin_bottom
            )
        )

        apply.setOnClickListener { presenter.onApplyClick() }
    }

    override fun setToolbarTitle(titleResId: Int) {
        detail_routes_title.setSmallCaps(titleResId)
    }

    override fun showMsg(message: String) {
        route_root_container.longSnackbar(message)
    }

    override fun setLoaderVisibility(visible: Boolean) {
        if (visible) {
            detail_routes_loader.setVisible()
            detail_routes_recycler_view.setGone()
            apply.setGone()
        } else {
            detail_routes_loader.setGone()
            detail_routes_recycler_view.setVisible()
            apply.setVisible()
        }
    }

    override fun openExternalUrl(url: String) {
        requireContext().openInCustomBrowser(url)
    }

    override fun setApplyBtn(primaryText: String, secondaryText: String) {
        val builder = SpannableStringBuilder("$primaryText\n")

        val spannable = SpannableString(secondaryText).apply {
            setSpan(RelativeSizeSpan(0.75F), 0, this.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
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

    override fun setSimpleApplyBtn(text: String) {
        apply.setVisible()
        apply.text = text
    }

    override fun showPriceInfoAlert(description: String) {
        dialog = DefaultAlertDialog(requireContext())
            .setTitle(R.string.ticket_price_title)
            .setMessage(description)
            .setPositiveButton(R.string.ok) { presenter.onOkPriceInfoClick() }
            .setOnDismissListener { presenter.onDismissAlertDialog() }
            .build()
        dialog?.show()
    }

    override fun hideAlertDialog() {
        dialog?.cancel()
        dialog = null
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun updateItems(route: Route, tripPlaces: Map<Long, TripPlace>) {
        adapter.updateItems(route, tripPlaces)
    }

    override fun clearUI() {
        super.clearUI()
        detail_routes_recycler_view.adapter = null
        dialog?.dismiss()
        dialog = null
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

    override fun showMsg(messageResId: Int) {
        route_root_container.shortSnackbar(getString(messageResId))
    }
}