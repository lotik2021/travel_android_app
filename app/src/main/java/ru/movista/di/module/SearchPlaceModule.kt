package ru.movista.di.module

import dagger.Module
import dagger.Provides
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.ChoosePlaceUseCase
import ru.movista.domain.usecase.IChoosePlaceUseCase
import ru.movista.domain.usecase.PlaceFoundUseCase
import ru.movista.domain.usecase.SearchPlaceUseCase

@Module
class SearchPlaceModule {
    @Provides
    @FragmentScope
    fun provideChoosePlaceUseCase(choosePlaceUseCase: ChoosePlaceUseCase): IChoosePlaceUseCase {
        return choosePlaceUseCase
    }

    @Provides
    @FragmentScope
    fun providePlaceFoundUseCase(choosePlaceUseCase: ChoosePlaceUseCase): PlaceFoundUseCase {
        return choosePlaceUseCase
    }

    @Provides
    @FragmentScope
    fun provideSearchPlaceUseCase(choosePlaceUseCase: ChoosePlaceUseCase): SearchPlaceUseCase {
        return choosePlaceUseCase
    }
}