package ru.movista.presentation.documents

import moxy.InjectViewState
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.data.source.local.AGREEMENT_URL
import ru.movista.data.source.local.PERSONAL_DATA_POLICY_URL
import ru.movista.data.source.local.PRIVACY_URL
import ru.terrakok.cicerone.Router

@InjectViewState
class LegalDocumentsPresenter : BasePresenter<LegalDocumentsView>() {

    override val screenTag: String
        get() = Screens.LegalDocuments.TAG

    private var router: Router? = null

    override fun onPresenterInject() {
        router = Injector.mainComponent?.getRouter()
    }

    fun onBackClicked() {
        router?.exit()
    }

    fun onUserAgreementClick() {
        viewState.openInCustomBrowser(AGREEMENT_URL)
    }

    fun onPrivacyPolicyClick() {
        viewState.openInCustomBrowser(PRIVACY_URL)
    }

    fun onPolicyOfPersonalDataClick() {
        viewState.openInCustomBrowser(PERSONAL_DATA_POLICY_URL)
    }
}