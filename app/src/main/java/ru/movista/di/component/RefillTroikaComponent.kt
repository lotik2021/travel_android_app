package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.presentation.refilltravelcard.refilltroika.RefillTroikaPresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface RefillTroikaComponent {
    fun inject(refillTroikaPresenter: RefillTroikaPresenter)
}