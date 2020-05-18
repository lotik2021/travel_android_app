package ru.movista.presentation.tickets.passengers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.dialog_edit_child_info.*
import kotlinx.android.synthetic.main.layout_choose_count.view.*
import org.jetbrains.anko.imageResource
import ru.movista.R
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.presentation.utils.setSmallCaps

class ChildInfoDialogFragment : BaseDialogFragment() {

    companion object {
        private const val KEY_CHILD_INFO = "child_info"

        fun newInstance(childInfoViewModel: ChildInfoViewModel?): ChildInfoDialogFragment {
            return ChildInfoDialogFragment().apply {
                arguments = bundleOf(KEY_CHILD_INFO to childInfoViewModel)
            }
        }
    }

    private var presenter: PassengersPresenter? = null

    private var initialChildInfo: ChildInfoViewModel? = null

    private var chosenAge: Int = 6

    private var isSeatRequired = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_edit_child_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = (parentFragment as? PassengersFragment)?.presenter

        initialChildInfo = arguments?.getSerializable(KEY_CHILD_INFO) as? ChildInfoViewModel

        initialChildInfo?.let {
            chosenAge = it.age
            isSeatRequired = it.seatRequired
        }

        child_info_with_seat_switch.setOnCheckedChangeListener { _, isChecked ->
            isSeatRequired = isChecked
        }

        initUI()
    }

    private fun initUI() {
        child_info_alert.setSmallCaps(R.string.child_info_alert)

        child_info_done_button.setOnClickListener {
            presenter?.onChildInfoDoneButtonClicked(chosenAge, isSeatRequired)
            postOnMainThread { dismiss() }
        }

        child_info_choose_adult_count.increase_count.setOnClickListener {
            if (chosenAge == 17) return@setOnClickListener

            chosenAge++

            if (chosenAge > 6) isSeatRequired = true

            updateViewState()
        }

        child_info_choose_adult_count.decrease_count.setOnClickListener {
            if (chosenAge == 0) return@setOnClickListener

            chosenAge--

            updateViewState()
        }

        updateViewState()
    }

    private fun updateViewState() {

        updateClickableStates()

        child_info_choose_adult_count.count_text.text = if (chosenAge == 0) "< 1" else chosenAge.toString()
        // чтобы не было подлагиваний анимации
        if (child_info_with_seat_switch.isChecked != isSeatRequired) {
            child_info_with_seat_switch.isChecked = isSeatRequired
        }
    }

    private fun updateClickableStates() {
        when (chosenAge) {
            0 -> {
                setInactiveDecreaseButton()
                child_info_choose_adult_count.decrease_count.isClickable = false
            }
            in 1..6 -> {
                setActiveIncreaseButton()
                setActiveDecreaseButton()

                child_info_choose_adult_count.decrease_count.isClickable = true
                child_info_choose_adult_count.increase_count.isClickable = true
                child_info_with_seat_switch.isClickable = true

                fadeInSeatSetting()
            }
            in 7..16 -> {
                setActiveIncreaseButton()
                setActiveDecreaseButton()

                child_info_choose_adult_count.decrease_count.isClickable = true
                child_info_choose_adult_count.increase_count.isClickable = true
                child_info_with_seat_switch.isClickable = false
                fadeOutSeatSetting()

            }
            17 -> {
                setInactiveIncreaseButton()
                child_info_choose_adult_count.increase_count.isClickable = false
            }
        }
    }

    private fun setActiveIncreaseButton() {
        child_info_choose_adult_count.increase_count.imageResource = R.drawable.add_active
    }

    private fun setInactiveIncreaseButton() {
        child_info_choose_adult_count.increase_count.imageResource = R.drawable.add_inactive
    }

    private fun setActiveDecreaseButton() {
        child_info_choose_adult_count.decrease_count.imageResource = R.drawable.remove_active
    }

    private fun setInactiveDecreaseButton() {
        child_info_choose_adult_count.decrease_count.imageResource = R.drawable.remove_inactive
    }

    private fun fadeInSeatSetting() {
        if (child_info_with_seat_title.alpha > 0.3f) return // анимация уже идет
        child_info_with_seat_title.animate().alpha(1f).start()
        child_info_with_seat_switch.animate().alpha(1f).start()
    }

    private fun fadeOutSeatSetting() {
        if (child_info_with_seat_title.alpha < 1f) return // анимация уже идет
        child_info_with_seat_title.animate().alpha(0.3f).start()
        child_info_with_seat_switch.animate().alpha(0.3f).start()

    }
}