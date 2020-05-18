package ru.movista.domain.usecase.tickets

import io.reactivex.subjects.PublishSubject
import ru.movista.data.source.local.models.RouteFilterType
import ru.movista.di.FragmentScope
import ru.movista.domain.model.tickets.Route
import ru.movista.presentation.tickets.segments.RoutePlaceFilter
import ru.movista.presentation.tickets.segments.UserRouteFilter
import javax.inject.Inject

@FragmentScope
class SegmentsUseCase @Inject constructor() {
    private val routSelectedSubject = PublishSubject.create<Route>()
    private val filterSelectedSubject = PublishSubject.create<Pair<UserRouteFilter, List<Route>>>()
    private val routePlaceFilterSubject = PublishSubject.create<Pair<RouteFilterType, List<RoutePlaceFilter>>>()

    fun getRoutSelectedObservable() = routSelectedSubject

    fun sendRoutSelected(route: Route) = routSelectedSubject.onNext(route)


    fun getFilterSelectedObservable() = filterSelectedSubject

    fun applyFilterSelected(filters: UserRouteFilter, routes: List<Route>) {
        filterSelectedSubject.onNext(filters to routes)
    }

    fun getRoutePlaceFilterObservable() = routePlaceFilterSubject

    fun applyRoutePlaceFilter(routeFilterType: RouteFilterType, routes: List<RoutePlaceFilter>) {
        routePlaceFilterSubject.onNext(routeFilterType to routes)
    }
}