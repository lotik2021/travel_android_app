package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.presentation.common.mapview.MapPresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface MapComponent {
    fun inject(mapPresenter: MapPresenter)
}