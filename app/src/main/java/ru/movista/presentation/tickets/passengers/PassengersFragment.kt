package ru.movista.presentation.tickets.passengers

import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.fragment_passengers.*
import kotlinx.android.synthetic.main.layout_child_info.view.*
import kotlinx.android.synthetic.main.layout_choose_count.view.*
import moxy.presenter.InjectPresenter
import org.jetbrains.anko.imageResource
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener

class PassengersFragment : BaseFragment(), PassengersView, OnBackPressedListener {
    companion object {
        private const val TAG_CHILD_INFO_BOTTOM_DIALOG = "child_info_bottom_dialog"

        fun newInstance() = PassengersFragment()
    }

    override fun getLayoutRes() = R.layout.fragment_passengers

    @InjectPresenter
    lateinit var presenter: PassengersPresenter

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(passengers_toolbar)

        passengers_choose_adult_count.increase_count.setOnClickListener {
            presenter.onIncreaseAdultCountClicked()
        }

        passengers_choose_adult_count.decrease_count.setOnClickListener {
            presenter.onDecreaseAdultCountClicked()
        }

        passengers_choose_children_count.increase_count.setOnClickListener {
            presenter.onIncreaseChildrenCountClicked()
        }

        passengers_choose_children_count.decrease_count.setOnClickListener {
            presenter.onDecreaseChildrenCountClicked()
        }

        passengers_done_button.setOnClickListener { presenter.onDoneButtonClicked() }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun showPassengersInfo(passengersInfoViewModel: PassengersInfoViewModel) {
        passengers_choose_adult_count.count_text.text = passengersInfoViewModel.adultCount.toString()
        passengers_choose_children_count.count_text.text = passengersInfoViewModel.childrenInfo.size.toString()
        updateChildrenInfo(passengersInfoViewModel.childrenInfo)
    }

    override fun showChildInfoDialog(childInfoViewModel: ChildInfoViewModel?) {
        val childInfoDialogFragment =
            ChildInfoDialogFragment.newInstance(childInfoViewModel)

        childInfoDialogFragment.show(childFragmentManager, TAG_CHILD_INFO_BOTTOM_DIALOG)
    }

    override fun disableIncreaseAdultCountIf(condition: Boolean) {
        passengers_choose_adult_count.increase_count.isClickable = !condition
        passengers_choose_adult_count.increase_count.imageResource =
            if (condition) R.drawable.add_inactive else R.drawable.add_active

    }

    override fun disableIncreaseChildrenCountIf(condition: Boolean) {
        passengers_choose_children_count.increase_count.isClickable = !condition
        passengers_choose_children_count.increase_count.imageResource =
            if (condition) R.drawable.add_inactive else R.drawable.add_active
    }

    override fun disableDecreaseAdultCountIf(condition: Boolean) {
        passengers_choose_adult_count.decrease_count.isClickable = !condition
        passengers_choose_adult_count.decrease_count.imageResource =
            if (condition) R.drawable.remove_inactive else R.drawable.remove_active
    }

    override fun disableDecreaseChildrenCountIf(condition: Boolean) {
        passengers_choose_children_count.decrease_count.isClickable = !condition
        passengers_choose_children_count.decrease_count.imageResource =
            if (condition) R.drawable.remove_inactive else R.drawable.remove_active
    }

    override fun showMsg(@StringRes messageResId: Int) {
        showMessage(passengers_content, messageResId)
    }

    private fun updateChildrenInfo(childrenInfoViewModel: List<ChildInfoViewModel>) {
        if (passengers_children_info_layout.childCount > 0) passengers_children_info_layout.removeAllViews()

        childrenInfoViewModel.forEachIndexed { index, childInfo ->
            val childCardView = createChildInfoLayout().apply {
                id = index
                child_info_chip_text.text = "${childInfo.ageLabel}, ${childInfo.seatLabel}"
                setOnClickListener { presenter.onChildInfoChipClicked(index) }
            }
            passengers_children_info_layout.addView(
                childCardView
            )
        }
    }

    private fun createChildInfoLayout(): CardView {
        val childCardView = layoutInflater.inflate(R.layout.layout_child_info, null) as CardView
        val params = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(
                0,
                0,
                resources.getDimensionPixelSize(R.dimen.size_small_plus),
                resources.getDimensionPixelSize(R.dimen.size_small_plus)
            )
        }
        childCardView.layoutParams = params
        return childCardView
    }
}
