package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.ProfileUseCase
import ru.movista.presentation.profile.ProfilePresenter
import ru.movista.presentation.profile.edit.EditProfilePresenter
import ru.movista.presentation.profile.favorites.FavoritesPresenter
import ru.movista.presentation.profile.transporttypes.TransportTypesPresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface ProfileComponent {
    fun getProfileUseCase() : ProfileUseCase

    fun inject(presenter: ProfilePresenter)
    fun inject(presenter: EditProfilePresenter)
    fun inject(presenter: TransportTypesPresenter)
    fun inject(presenter: FavoritesPresenter)
}