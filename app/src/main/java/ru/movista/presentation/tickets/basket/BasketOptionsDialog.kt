package ru.movista.presentation.tickets.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.dialog_basket_item_options.*
import ru.movista.R
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.viewmodel.SegmentViewModel

class BasketOptionsDialog : BaseDialogFragment() {
    companion object {
        private const val BASKET_MODEL_KEY = "basket_model_key"

        fun newInstance(segmentViewModel: SegmentViewModel): BasketOptionsDialog {
            return BasketOptionsDialog().apply {
                arguments = bundleOf(BASKET_MODEL_KEY to segmentViewModel)
            }
        }
    }

    private lateinit var segmentModel: SegmentViewModel
    private lateinit var presenter: BasketPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_basket_item_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = (parentFragment as? BasketFragment)?.presenter ?: return
        segmentModel = arguments?.getParcelable(BASKET_MODEL_KEY) ?: return

        initUI()
    }

    private fun initUI() {
        primary_text.text = segmentModel.description
        secondary_text.text = segmentModel.subDescription

        trip_detail.setOnClickListener {
            presenter.onTripDetailClick(segmentModel)
            dismiss()
        }

        trip_change.setOnClickListener {
            presenter.onTripChangeClick(segmentModel)
            dismiss()
        }
    }
}