package ru.movista.di.module.tickets

import dagger.Module
import dagger.Provides
import ru.movista.data.repository.tickets.SearchPlacesRepository
import ru.movista.di.FlowScope
import ru.movista.di.FragmentScope
import ru.movista.domain.model.tickets.SearchModel
import ru.movista.domain.usecase.tickets.searchparams.*


@Module
class SearchTicketsModule {

    @Provides
    @FlowScope
    fun provideSearchTicketsRepository(): ISearchParamsRepository {
        return StubRepository()
    }

    @Provides
    @FlowScope
    fun provideSearchUseCase(
        repository: ISearchParamsRepository,
        placesRepository: SearchPlacesRepository
    ): SearchTicketsUseCase {
        return SearchTicketsUseCase(repository, placesRepository)
    }

    @Provides
    @FragmentScope
    fun provideEmptySearchModel(): SearchModel {
        return SearchModel()
    }

    @Provides
    @FlowScope
    fun provideTicketsSearchUseCase(
        searchUseCase: SearchTicketsUseCase
    ): ISearchTicketsUseCase {
        return searchUseCase
    }


    @Provides
    @FlowScope
    fun providePlaceSearchUseCase(
        searchUseCase: SearchTicketsUseCase
    ): IPlaceSelectUseCase {
        return searchUseCase
    }

    @Provides
    @FlowScope
    fun provideDateSelectUseCase(
        searchUseCase: SearchTicketsUseCase
    ): IDateSelectUseCase {
        return searchUseCase
    }


    @Provides
    @FlowScope
    fun providePassengersInfoUseCase(
        searchUseCase: SearchTicketsUseCase
    ): IPassengersUseCase {
        return searchUseCase
    }

    @Provides
    @FlowScope
    fun provideComfortTypeUseCase(
        searchUseCase: SearchTicketsUseCase
    ): IComfortTypeUseCase {
        return searchUseCase
    }

}