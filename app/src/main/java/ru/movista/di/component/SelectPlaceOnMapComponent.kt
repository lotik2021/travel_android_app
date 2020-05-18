package ru.movista.di.component

import dagger.Component
import ru.movista.di.module.MapModule
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.IChoosePlaceUseCase
import ru.movista.domain.usecase.PlaceFoundUseCase
import ru.movista.presentation.selectplaceonmap.SelectPlaceOnMapFragment
import ru.movista.presentation.selectplaceonmap.SelectPlaceOnMapPresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class],
    modules = [MapModule::class]
)
interface SelectPlaceOnMapComponent {

    fun getChoosePlaceUseCase(): IChoosePlaceUseCase
    fun getPlaceFoundUseCase(): PlaceFoundUseCase

    fun inject(selectPlaceOnMapFragment: SelectPlaceOnMapFragment)
    fun inject(selectPlaceOnMapPresenter: SelectPlaceOnMapPresenter)
}