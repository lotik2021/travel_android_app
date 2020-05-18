package ru.movista.presentation.auth.enterphone

import com.google.android.gms.safetynet.SafetyNet
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import kotlinx.android.synthetic.main.fragment_enter_phone.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.utils.longSnackbar
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible
import ru.movista.presentation.utils.showSoftKeyboard

class EnterPhoneFragment : BaseFragment(), EnterPhoneView, OnBackPressedListener {

    companion object {
        private const val PRIMARY_FORMAT = "+7 ([000]) [000]-[00]-[00]"

        fun newInstance(): EnterPhoneFragment {
            return EnterPhoneFragment()
        }
    }

    @InjectPresenter
    lateinit var presenter: EnterPhonePresenter

    override fun getLayoutRes() = R.layout.fragment_enter_phone

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(enter_phone_toolbar)

        setDefaultPhoneState()

        enter_phone_next_button.setOnClickListener { presenter.onNextButtonClicked() }

        enter_phone_edit_text.requestFocus()
        enter_phone_edit_text.showSoftKeyboard()

    }

    override fun showLoading() {
        enter_phone_content.setGone()
        enter_phone_loading.setVisible()
    }

    override fun hideLoading() {
        enter_phone_loading.setGone()
        enter_phone_content.setVisible()
    }

    override fun showError(error: String) {
        enter_phone_parent.longSnackbar(error)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun setEnterPhoneNextButtonEnabled(enabled: Boolean) {
        enter_phone_next_button.isEnabled = enabled
    }

    override fun verifyWithRecaptcha(siteKey: String) {
        activity?.let { activity ->
            SafetyNet.getClient(activity).verifyWithRecaptcha(siteKey)
                .addOnSuccessListener(activity) { response ->
                    presenter.onRecaptchaSuccess(response.tokenResult)
                }
                .addOnFailureListener(activity) { ex ->
                    presenter.onRecaptchaFailure(ex)
                }
        }
    }

    private fun setDefaultPhoneState() {
        MaskedTextChangedListener.installOn(
            editText = enter_phone_edit_text,
            primaryFormat = PRIMARY_FORMAT,
            affinityCalculationStrategy = AffinityCalculationStrategy.PREFIX,
            valueListener = object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                    presenter.onPhoneNumberChanged(maskFilled, extractedValue)
                }
            }
        )
    }
}
