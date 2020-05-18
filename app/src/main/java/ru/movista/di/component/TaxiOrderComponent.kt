package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.presentation.taxiorder.TaxiOrderDialogFragment

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface TaxiOrderComponent {
    fun inject(taxiOrderDialogFragment: TaxiOrderDialogFragment)
}