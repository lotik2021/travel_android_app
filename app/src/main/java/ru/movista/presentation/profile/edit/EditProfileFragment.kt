package ru.movista.presentation.profile.edit

import kotlinx.android.synthetic.main.fragment_edit_profile.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment

class EditProfileFragment : BaseFragment(), EditProfileView {

    companion object {
        fun newInstance() = EditProfileFragment()
    }

    @InjectPresenter
    lateinit var presenter: EditProfilePresenter

    override fun getLayoutRes() = R.layout.fragment_edit_profile

    override fun initUI() {
        super.initUI()

        profile_edit_toolbar.setNavigationOnClickListener { presenter.onBackClicked() }
    }
}