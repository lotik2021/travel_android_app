package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.presentation.routedetail.RouteDetailPresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface RouteDetailComponent {
    fun inject(routeDetailPresenter: RouteDetailPresenter)
}
