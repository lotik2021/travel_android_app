package ru.movista.presentation.documents

import kotlinx.android.synthetic.main.fragment_legal_documents.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.utils.openInCustomBrowser

class LegalDocumentsFragment : BaseFragment(), LegalDocumentsView {

    companion object {
        fun newInstance() = LegalDocumentsFragment()
    }

    @InjectPresenter
    lateinit var presenter: LegalDocumentsPresenter

    override fun getLayoutRes() = R.layout.fragment_legal_documents

    override fun initUI() {
        super.initUI()

        app_toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        user_agreement.setOnClickListener { presenter.onUserAgreementClick() }
        privacy_policy.setOnClickListener { presenter.onPrivacyPolicyClick() }
        policy_of_personal_data.setOnClickListener { presenter.onPolicyOfPersonalDataClick() }
    }

    override fun openInCustomBrowser(url: String) {
        context.openInCustomBrowser(url)
    }
}
