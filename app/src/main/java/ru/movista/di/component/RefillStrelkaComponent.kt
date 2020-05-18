package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.presentation.refilltravelcard.refillstrelka.RefillStrelkaPresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface RefillStrelkaComponent {
    fun inject(refillStrelkaPresenter: RefillStrelkaPresenter)
}