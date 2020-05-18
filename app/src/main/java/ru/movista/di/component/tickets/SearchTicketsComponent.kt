package ru.movista.di.component.tickets

import android.content.res.Resources
import dagger.Component
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.component.MainComponent
import ru.movista.di.module.FlowNavigationModule
import ru.movista.di.module.tickets.SearchTicketsModule
import ru.movista.di.FlowScope
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.tickets.searchparams.IComfortTypeUseCase
import ru.movista.domain.usecase.tickets.searchparams.IDateSelectUseCase
import ru.movista.domain.usecase.tickets.searchparams.IPassengersUseCase
import ru.movista.domain.usecase.tickets.searchparams.IPlaceSelectUseCase
import ru.movista.presentation.tickets.dateselect.DateSelectPresenter
import ru.movista.presentation.tickets.passengers.PassengersPresenter
import ru.movista.presentation.tickets.placeselect.PlaceSelectPresenter
import ru.movista.presentation.tickets.searchtickets.SearchTicketsPresenter
import ru.terrakok.cicerone.Router

@FlowScope
@Component(
    dependencies = [MainComponent::class],
    modules = [SearchTicketsModule::class, FlowNavigationModule::class]
)
interface SearchTicketsComponent {
    fun provideRouter(): Router
    fun provideResources(): Resources
    fun provideAnalyticManager(): AnalyticsManager

    fun provideDateSelectUseCase(): IDateSelectUseCase
    fun providePassengersUseCase(): IPassengersUseCase
    fun provideComfortTypesUseCase(): IComfortTypeUseCase
    fun provideCitySelectUseCase(): IPlaceSelectUseCase

    fun inject(searchTicketsPresenter: SearchTicketsPresenter)
}

@FragmentScope
@Component(
    dependencies = [SearchTicketsComponent::class]
)
interface DateSelectComponent {
    fun inject(dateSelectPresenter: DateSelectPresenter)
}

@FragmentScope
@Component(
    dependencies = [SearchTicketsComponent::class]
)
interface PassengersComponent {
    fun inject(passengersPresenter: PassengersPresenter)
}

@FragmentScope
@Component(
    dependencies = [SearchTicketsComponent::class]
)
interface PlaceSelectComponent {
    fun inject(placeSelectPresenter: PlaceSelectPresenter)
}