package ru.movista.presentation.profile.transporttypes

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_transport_types.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.domain.model.ProfileTransportType
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.custom.HorizontalDivider
import ru.movista.presentation.utils.longSnackbar
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible

class TransportTypesFragment : BaseFragment(), TransportTypesView {

    companion object {
        private const val KEY_TYPES = "profile_transport_types"

        fun newInstance(profileTransportTypes: List<ProfileTransportType>): TransportTypesFragment {
            return TransportTypesFragment().apply {
                arguments = bundleOf(KEY_TYPES to profileTransportTypes)
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: TransportTypesPresenter

    @ProvidePresenter
    fun providePresenter(): TransportTypesPresenter = TransportTypesPresenter(arguments?.getSerializable(KEY_TYPES))

    private lateinit var adapter: TransportTypesAdapter

    override fun getLayoutRes() = R.layout.fragment_transport_types

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TransportTypesAdapter { item, isChecked -> presenter.onTransportCheckedChange(item, isChecked) }
    }

    override fun initUI() {
        super.initUI()

        transport_types_toolbar.setNavigationOnClickListener { presenter.onBackClicked() }
        transport_types_save_button.setOnClickListener { presenter.onSaveClick() }

        transport_types_recycler_view.adapter = adapter
        transport_types_recycler_view.layoutManager = LinearLayoutManager(context)
        transport_types_recycler_view.addItemDecoration(
            HorizontalDivider(
                context = requireContext(),
                leftMarginRes = R.dimen.size_medium
            )
        )
    }

    override fun setTransportTypes(transportsTypes: List<ProfileTransportType>) {
        adapter.setItems(transportsTypes)
    }


    override fun showLoading() {
        transport_types_loading.setVisible()
        transport_types_save_button.setGone()
        transport_types_recycler_view.setGone()
    }

    override fun hideLoading() {
        transport_types_loading.setGone()
        transport_types_save_button.setVisible()
        transport_types_recycler_view.setVisible()
    }

    override fun showError(error: String) {
        view?.longSnackbar(error)
    }
}