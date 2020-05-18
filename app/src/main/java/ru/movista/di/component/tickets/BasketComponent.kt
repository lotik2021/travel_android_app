package ru.movista.di.component.tickets

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.di.component.MainComponent
import ru.movista.presentation.tickets.basket.BasketPresenter

@FragmentScope
@Component(dependencies = [MainComponent::class])
interface BasketComponent {
    fun inject(presenter: BasketPresenter)
}