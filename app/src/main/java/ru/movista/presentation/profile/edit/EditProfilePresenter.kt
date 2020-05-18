package ru.movista.presentation.profile.edit

import moxy.InjectViewState
import ru.movista.di.Injector
import ru.movista.domain.usecase.ProfileUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class EditProfilePresenter : BasePresenter<EditProfileView>() {
    override val screenTag: String
        get() = Screens.EditProfile.TAG

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var profileUseCase: ProfileUseCase

    override fun onPresenterInject() {
        Injector.init(Screens.Profile.TAG)
        Injector.profileComponent?.inject(this)
    }

    fun onBackClicked() {
        router.exit()
    }
}