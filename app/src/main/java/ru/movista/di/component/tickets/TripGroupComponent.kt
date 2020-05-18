package ru.movista.di.component.tickets

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.di.component.MainComponent
import ru.movista.presentation.tickets.tripgroup.TripGroupPresenter

@FragmentScope
@Component(dependencies = [MainComponent::class])
interface TripGroupComponent {
    fun inject(presenter: TripGroupPresenter)
}
