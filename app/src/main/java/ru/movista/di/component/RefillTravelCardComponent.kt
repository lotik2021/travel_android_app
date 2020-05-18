package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.presentation.refilltravelcard.RefillTravelCardDialogFragment

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface RefillTravelCardComponent {
    fun inject(refillTravelCardDialogFragment: RefillTravelCardDialogFragment)
}