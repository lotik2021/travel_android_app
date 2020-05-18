package ru.movista.presentation.tickets.segments.sorting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_sorting_options_dialog.*
import ru.movista.R
import ru.movista.data.source.local.models.TicketSortingOption
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.custom.HorizontalDivider
import ru.movista.presentation.tickets.segments.SegmentsFragment
import ru.movista.presentation.tickets.segments.SegmentsPresenter

class SortingOptionsDialog : BaseDialogFragment() {
    companion object {
        private const val SORTING_OPTION_KEY = "sorting_option_key"

        fun newInstance(sortingOption: TicketSortingOption): SortingOptionsDialog {
            return SortingOptionsDialog()
                .apply {
                    arguments = bundleOf(SORTING_OPTION_KEY to sortingOption)
                }
        }
    }

    private lateinit var sortingOption: TicketSortingOption
    private lateinit var presenter: SegmentsPresenter
    private lateinit var adapter: SortingOptionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = (parentFragment as? SegmentsFragment)?.presenter ?: return
        sortingOption = arguments?.getParcelable(SORTING_OPTION_KEY) ?: TicketSortingOption.CHEAPER_FIRST

        adapter = SortingOptionsAdapter(
            TicketSortingOption.values().toList(),
            sortingOption
        ) { option ->
            presenter.onSortingOptionClick(option)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_sorting_options_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        sorting_recycler_view.hasFixedSize()
        sorting_recycler_view.layoutManager = LinearLayoutManager(context)
        sorting_recycler_view.adapter = adapter
        sorting_recycler_view.addItemDecoration(
            HorizontalDivider(
                context = requireContext(),
                leftMarginRes = R.dimen.size_medium
            )
        )
    }
}