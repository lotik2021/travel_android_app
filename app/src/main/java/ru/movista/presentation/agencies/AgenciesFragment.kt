package ru.movista.presentation.agencies

import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.fragment_agencies.*
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.utils.openInCustomBrowser
import ru.movista.presentation.utils.openPhoneDial
import ru.movista.presentation.viewmodel.AgencyViewModel
import ru.terrakok.cicerone.Router

class AgenciesFragment : BaseFragment() {

    companion object {
        private const val KEY_AGENCIES = "key_agencies"

        fun newInstance(agencies: List<AgencyViewModel>): AgenciesFragment {
            return AgenciesFragment().apply {
                arguments = bundleOf(KEY_AGENCIES to agencies)
            }
        }
    }

    lateinit var router: Router

    lateinit var agencies: List<AgencyViewModel>

    override fun getLayoutRes() = R.layout.fragment_agencies

    override fun onFragmentInject() {
        super.onFragmentInject()

        Injector.mainComponent?.inject(this)
    }

    override fun onDestroy() {

        setTranslucentStatusBar() // обратно устанавливаем прозрачность

        super.onDestroy()
    }

    @Suppress("UNCHECKED_CAST")
    override fun initUI() {
        super.initUI()

        agencies_toolbar.navigationIcon = resources.getDrawable(R.drawable.vector_drawable_back, null)
        agencies_toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        arguments?.let {
            agencies = it.getSerializable(KEY_AGENCIES) as List<AgencyViewModel>
        } ?: return

        setupAgenciesRecyclerView()
    }

    private fun setupAgenciesRecyclerView() {
        agencies_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        val adapter = AgenciesAdapter(
            agencies,
            { activity?.openInCustomBrowser(agencies[it].url) },
            { activity?.openPhoneDial("tel:${agencies[it].phoneNumber}") }
        )
        agencies_recycler_view.adapter = adapter
    }
}
