package ru.movista.presentation.auth.verifyphone

import android.text.Html
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.fragment_verify_phone.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.utils.longSnackbar
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.utils.showSoftKeyboard

class VerifyPhoneFragment : BaseFragment(), VerifyPhoneView, OnBackPressedListener {

    companion object {
        fun newInstance(): VerifyPhoneFragment {
            return VerifyPhoneFragment()
        }
    }

    @InjectPresenter
    lateinit var presenter: VerifyPhonePresenter

    override fun getLayoutRes() = R.layout.fragment_verify_phone

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(verify_phone_toolbar)

        addDisposable(
            verify_phone_edit_text.textChanges()
                .subscribe { entered ->
                    verify_phone_next_button.isEnabled = (entered.length == 6)
                }
        )

        verify_phone_next_button.setOnClickListener {
            verify_phone_edit_text.text?.let {
                presenter.onNextButtonClicked(it.toString())
            }
        }

        verify_phone_resend_code.setOnClickListener { presenter.onResendCodeClicked() }

        verify_phone_edit_text.requestFocus()
        verify_phone_edit_text.showSoftKeyboard()
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun showLoading() {
        verify_phone_content.setGone()
        verify_phone_loading.setVisible()
    }

    override fun hideLoading() {
        verify_phone_content.setVisible()
        verify_phone_loading.setGone()
    }

    override fun showError(error: String) {
        view?.longSnackbar(error)
    }

    override fun changeResendCodeText(text: String, isClickable: Boolean) {
        verify_phone_resend_code.text = Html.fromHtml(text)
        verify_phone_resend_code.isClickable = isClickable
    }
}
