package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.tickets.SegmentsUseCase
import ru.movista.presentation.tickets.detailroute.DetailRoutesPresenter
import ru.movista.presentation.tickets.segments.SegmentsPresenter
import ru.movista.presentation.tickets.segments.providersfilter.RoutePlaceFilterPresenter
import ru.movista.presentation.tickets.segments.segmentfilter.SegmentFilterPresenter

@FragmentScope
@Component(dependencies = [MainComponent::class])
interface SegmentsComponent {
    fun getSegmentsUseCase(): SegmentsUseCase

    fun inject(presenter: SegmentsPresenter)
    fun inject(presenter: SegmentFilterPresenter)
    fun inject(presenter: RoutePlaceFilterPresenter)
    fun inject(presenter: DetailRoutesPresenter)
}
