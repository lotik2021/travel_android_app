package ru.movista.presentation.profile

import android.view.View
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*
import moxy.presenter.InjectPresenter
import ru.movista.BuildConfig
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.utils.*
import ru.movista.data.source.local.FILE_LOG_NAME
import java.io.File

class ProfileFragment : BaseFragment(), ProfileView, OnBackPressedListener {
    companion object {
        fun newInstance() = ProfileFragment()
    }

    @InjectPresenter
    lateinit var presenter: ProfilePresenter
    private var snackBar: Snackbar? = null

    override fun getLayoutRes() = R.layout.fragment_profile

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(profile_toolbar)

        feedback.setOnClickListener { presenter.onFeedbackClick() }
        legal_documents.setOnClickListener { presenter.onLegalDocumentsClick() }
        about_app.setOnClickListener { presenter.onAboutAppClick() }
        edit_profile.setOnClickListener { presenter.onEditProfileClick() }
        transport_types.setOnClickListener { presenter.onTransportTypesClick() }
        sign_in.setOnClickListener { presenter.onSignInClicked() }
        favorite_addresses.setOnClickListener { presenter.onFavoriteAddressesClick() }
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun setLoaderVisibility(visible: Boolean) {
        loader.setVisibility(visible)
    }

    override fun setEditProfileBtnVisibility(visible: Boolean) {
        edit_profile.setVisibility(visible)
    }

    override fun setInfoContentVisibility(visible: Boolean) {
        info_content.setVisibility(visible)
        info_title.setVisibility(visible)
    }

    override fun setRoutesContentVisibility(visible: Boolean) {
        routes_title.setVisibility(visible)
        routes_content.setVisibility(visible)
    }

    override fun setProfileContent() {
        un_auth_header_container.setGone()
        profile_header_container.setVisible()

        city.setGone()
        profile_ico.setGone()
    }

    override fun setUserName(userName: String) {
        user_name.text = userName
    }

    override fun setCity(userCity: String) {
        city.text = userCity
    }

    override fun setUnAuthContent() {
        un_auth_header_container.setVisible()
        profile_header_container.setGone()
        edit_profile.setGone()

        routes_title.setGone()
        routes_content.setGone()
    }

    override fun showMsg(msg: String) {
        snackBar?.dismiss()
        snackBar = root_container.infiniteSnackBar(msg, getString(R.string.update),
            View.OnClickListener { presenter.onSnackBarActionClick() }
        )
    }

    override fun hideSnackBar() {
        snackBar?.dismiss()
    }

    override fun navigateToFeedBackMailApplication(
        whom: String,
        versionName: String,
        versionCode: Int,
        brand: String,
        model: String,
        osVersion: String
    ) {
        val logFilePath = requireContext().filesDir
        val logFile = File(logFilePath, FILE_LOG_NAME)
        val logContentUri = try {
            FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, logFile)
        } catch (e: Exception) {
            null
        }
        context?.openInMailApplication(
            whom,
            getString(R.string.feed_back_title),
            getString(R.string.feed_back_content, versionName, "$brand $model", osVersion),
            getString(R.string.choose_email_client),
            logContentUri
        )
    }

    override fun clearUI() {
        super.clearUI()
        snackBar?.dismiss()
        snackBar = null
    }
}