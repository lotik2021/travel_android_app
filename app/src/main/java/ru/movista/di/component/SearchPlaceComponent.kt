package ru.movista.di.component

import dagger.Component
import ru.movista.di.module.SearchPlaceModule
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.IChoosePlaceUseCase
import ru.movista.domain.usecase.PlaceFoundUseCase
import ru.movista.domain.usecase.SearchPlaceUseCase
import ru.movista.presentation.placesearch.SearchPlaceFragment
import ru.movista.presentation.placesearch.SearchPlacePresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class],
    modules = [SearchPlaceModule::class]
)
interface SearchPlaceComponent {
    fun getChoosePlaceUseCase(): IChoosePlaceUseCase
    fun getSearchPlaceUseCase(): SearchPlaceUseCase
    fun getPlaceFoundUseCase(): PlaceFoundUseCase

    fun inject(searchPlaceFragment: SearchPlaceFragment)
    fun inject(searchPlacePresenter: SearchPlacePresenter)
}