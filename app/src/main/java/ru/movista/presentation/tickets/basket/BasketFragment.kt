package ru.movista.presentation.tickets.basket

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_basket.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.data.source.local.models.TripType
import ru.movista.domain.model.tickets.PathGroup
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.model.tickets.TripGroup
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.CustomTypefaceSpan
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.custom.HorizontalDivider
import ru.movista.presentation.custom.MarginDecoration
import ru.movista.presentation.utils.*
import ru.movista.presentation.viewmodel.SegmentViewModel

class BasketFragment : BaseFragment(), BasketView, OnBackPressedListener {
    companion object {
        private const val SEARCH_UID_KEY = "search_uid_key"
        private const val TRIP_GROUP_KEY = "trip_group_key"
        private const val SEARCH_PARAM_KEY = "search_param_key"
        private const val PATH_GROUP_KEY = "path_group_key"

        fun newInstance(
            searchParams: SearchModel?,
            searchUid: String?,
            tripGroup: TripGroup?,
            pathGroup: PathGroup?
        ): BasketFragment {
            return BasketFragment().apply {
                arguments = bundleOf(
                    SEARCH_PARAM_KEY to searchParams,
                    SEARCH_UID_KEY to searchUid,
                    TRIP_GROUP_KEY to tripGroup,
                    PATH_GROUP_KEY to pathGroup
                )
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: BasketPresenter
    private lateinit var adapter: BasketAdapter
    private var snackBar: Snackbar? = null

    @ProvidePresenter
    fun providePresenter(): BasketPresenter {
        return BasketPresenter(
            arguments?.getParcelable(SEARCH_PARAM_KEY) as SearchModel,
            arguments?.getString(SEARCH_UID_KEY) as String,
            arguments?.getSerializable(TRIP_GROUP_KEY) as TripGroup,
            arguments?.getParcelable(PATH_GROUP_KEY) as PathGroup
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = BasketAdapter(presenter.isShowBasketHint, searchResultActions)
    }

    override fun getLayoutRes() = R.layout.fragment_basket

    override fun initUI() {
        super.initUI()
        arguments?.clear() // todo приходится очищать bundle, так как крашиться из-за нехватики памяти - перетащить эти модели в другой класс

        setupToolbarBackButton(tickets_selection_toolbar)

        basket_recycler_view.layoutManager = LinearLayoutManager(context)
        basket_recycler_view.adapter = adapter
        basket_recycler_view.addItemDecoration(
            HorizontalDivider(
                context = requireContext(),
                leftMarginRes = R.dimen.tickets_selection_margin_start
            )
        )
        basket_recycler_view.addItemDecoration(
            MarginDecoration(
                context = requireContext(),
                bottomMarginRes = R.dimen.basket_list_margin_bottom
            )
        )

        buy_btn.setOnClickListener { presenter.onBuyBtnClick() }
    }

    override fun showErrorMsg(message: String, action: View.OnClickListener) {
        postOnMainThreadWithDelay(200) {
            snackBar = tickets_selection_root.infiniteSnackBar(
                message,
                getString(R.string.update),
                action
            )
        }
    }

    override fun showSimpleErrorMsg(message: String) {
        tickets_selection_root.longSnackbar(message)
    }

    override fun hideErrorMsg() {
        snackBar?.dismiss()
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun hideBuyBtn() {
        buy_btn.setGone()
    }

    override fun setBuyBtn(primaryText: String, secondaryText: String) {
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
        buy_btn.text = builder
        buy_btn.setVisible()
    }

    override fun setBuyBtnVisibility(visible: Boolean) {
        buy_btn.setVisibility(visible)
    }

    override fun setLoaderVisibility(visible: Boolean) {
        if (visible) {
            basket_recycler_view.setGone()
            toolbar_shadow.setInVisible()
            search_loader.setVisible()
            toolbar_loader.setVisible()
        } else {
            basket_recycler_view.setVisible()
            toolbar_shadow.setVisible()
            search_loader.setGone()
            toolbar_loader.setInVisible()
        }
    }

    override fun openExternalUrl(url: String) {
        requireContext().openInCustomBrowser(url)
    }

    override fun setRoutsIcons(items: List<List<TripType>>) {
        routs_icons.setItems(items)
    }

    override fun updateSegments(items: List<SegmentViewModel>) {
        adapter.updateItems(items)
    }

    override fun setDate(forthDateTitle: String, backDateTitle: String?) {
       adapter.setDate(forthDateTitle, backDateTitle)
    }

    override fun openSegmentOptions(segmentViewModel: SegmentViewModel) {
        val dialogFragment = BasketOptionsDialog.newInstance(segmentViewModel)
        dialogFragment.show(childFragmentManager, BasketOptionsDialog::class.java.name)
    }

    override fun removeBasketHint() {
        adapter.removeBasketHint()
    }

    override fun clearUI() {
        super.clearUI()
        basket_recycler_view.adapter = null
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

    private val searchResultActions = object : BasketAdapter.Actions {
        override fun onOptionItemClick(item: SegmentViewModel) {
            presenter.onOptionItemClick(item)
        }

        override fun onNotShowBasketHint() {
            presenter.onNotShowBasketHint()
        }

        override fun onItemClick(item: SegmentViewModel) {
            presenter.onSegmentClick(item)
        }
    }
}