package ru.movista.presentation.about

import kotlinx.android.synthetic.main.fragment_about_app.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.utils.openInCustomBrowser

class AboutAppFragment : BaseFragment(), AboutAppView {

    companion object {
        fun newInstance() = AboutAppFragment()
    }

    @InjectPresenter
    lateinit var presenter: AboutAppPresenter

    override fun getLayoutRes() = R.layout.fragment_about_app

    override fun initUI() {
        super.initUI()
        about_app_toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        google_play_reviews.setOnClickListener { presenter.onGooglePlayReviewsClick() }
        vk.setOnClickListener { presenter.onVkClick() }
        instagram.setOnClickListener { presenter.onInstagramClick() }
        ok.setOnClickListener { presenter.onOkClick() }
        facebook.setOnClickListener { presenter.onFacebookClick() }
        youtube.setOnClickListener { presenter.onYoutubeClick() }
        twitter.setOnClickListener { presenter.onTwitterClick() }
    }

    override fun setAppVersion(version: String) {
        app_version.text = getString(R.string.app_version_text, version)
    }

    override fun openInCustomBrowser(url: String) {
        context.openInCustomBrowser(url)
    }
}