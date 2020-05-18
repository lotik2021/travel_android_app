package ru.movista.presentation.tickets.comforttypeselect

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.dialog_select_comfort_type.*
import ru.movista.R
import ru.movista.domain.model.tickets.ComfortType
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.tickets.searchtickets.SearchTicketsFragment
import ru.movista.presentation.tickets.searchtickets.SearchTicketsPresenter
import ru.movista.presentation.utils.postOnMainThreadWithDelay


class SelectComfortTypeDialogFragment : BaseDialogFragment() {

    companion object {
        private const val KEY_SELECTED_INDEX = "selected_index"

        fun newInstance(initialComfortType: String): SelectComfortTypeDialogFragment {
            return SelectComfortTypeDialogFragment().apply {
                arguments = bundleOf(KEY_SELECTED_INDEX to initialComfortType)
            }
        }
    }

    private lateinit var comfortType: ComfortType

    private lateinit var presenter: SearchTicketsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_select_comfort_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = (parentFragment as? SearchTicketsFragment)?.presenter ?: return

        comfortType = arguments?.getString(KEY_SELECTED_INDEX)?.let { ComfortType.valueOf(it) } ?: ComfortType.ECONOMY

        initUI()
    }

    private fun initUI() {
        when (comfortType) {
            ComfortType.ECONOMY -> {
                comfort_type_economy_button.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.comfort_type_selected))
            }
            ComfortType.PREMIUM_ECONOMY -> {
                comfort_type_premium_economy_button.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.comfort_type_selected))
            }
            ComfortType.BUSINESS -> {
                comfort_type_business_button.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.comfort_type_selected))
            }
            ComfortType.FIRST_CLASS -> {
                comfort_type_first_class_button.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.comfort_type_selected))
            }
        }

        comfort_type_economy_button.setOnClickListener {
            onComfortTypeSelected(ComfortType.ECONOMY)
        }
        comfort_type_premium_economy_button.setOnClickListener {
            onComfortTypeSelected(ComfortType.PREMIUM_ECONOMY)
        }
        comfort_type_business_button.setOnClickListener {
            onComfortTypeSelected(ComfortType.BUSINESS)
        }
        comfort_type_first_class_button.setOnClickListener {
            onComfortTypeSelected(ComfortType.FIRST_CLASS)
        }
    }

    private fun onComfortTypeSelected(selectedType: ComfortType) {
        clearBackground()
        postOnMainThreadWithDelay(150L) {
            presenter.onComfortTypeSelected(selectedType)
            dismiss()
        }
    }

    private fun clearBackground() {
        comfort_type_economy_button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

        comfort_type_premium_economy_button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

        comfort_type_business_button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

        comfort_type_first_class_button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

    }
}