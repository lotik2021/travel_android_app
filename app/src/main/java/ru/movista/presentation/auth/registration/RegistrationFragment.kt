package ru.movista.presentation.auth.registration

import android.util.Patterns
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.fragment_registration.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.utils.longSnackbar
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.utils.showSoftKeyboard
import ru.movista.utils.EMPTY

class RegistrationFragment : BaseFragment(), RegistrationView, OnBackPressedListener {

    companion object {
        fun newInstance(): RegistrationFragment = RegistrationFragment()
    }

    @InjectPresenter
    lateinit var presenter: RegistrationPresenter

    override fun getLayoutRes() = R.layout.fragment_registration

    override fun initUI() {
        super.initUI()
        setupToolbarBackButton(registration_toolbar)

        addDisposable(
            registration_first_name_edit_text.textChanges()
                .subscribe { changeNextButtonState() }
        )

        addDisposable(
            registration_email_edit_text.textChanges()
                .subscribe { changeNextButtonState() }
        )

        registration_first_name_edit_text.requestFocus()
        registration_email_edit_text.showSoftKeyboard()

        registration_done_button.setOnClickListener {
            presenter.onRegisterButtonClicked(
                registration_first_name_edit_text.text.toString(),
                registration_last_name_edit_text.text?.toString() ?: String.EMPTY,
                registration_email_edit_text.text.toString()
            )
        }
    }

    override fun onBackPressed() {
        val backStackCount = parentFragment?.childFragmentManager?.backStackEntryCount ?: return
        presenter.onBackPressed(backStackCount == 0)
    }

    override fun showLoading() {
        registration_content.setGone()
        registration_done_button.setGone()
        registration_loading.setVisible()
    }

    override fun hideLoading() {
        registration_content.setVisible()
        registration_done_button.setVisible()
        registration_loading.setGone()
    }

    override fun showError(error: String) {
        view?.longSnackbar(error)
    }

    private fun changeNextButtonState() {
        registration_done_button.isEnabled = !registration_first_name_edit_text.text.isNullOrBlank()
                && !registration_email_edit_text.text.isNullOrBlank()
                && isActuallyEmail(registration_email_edit_text.text.toString())
    }

    private fun isActuallyEmail(string: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(string).matches()
    }
}
