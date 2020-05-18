package ru.movista.di.module

import dagger.Module
import dagger.Provides
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.ChoosePlaceUseCase
import ru.movista.domain.usecase.SearchPlaceUseCase

@Module
class ChatModule {

    @Provides
    @FragmentScope
    fun provideSearchPlaceUseCase(choosePlaceUseCase: ChoosePlaceUseCase): SearchPlaceUseCase {
        return choosePlaceUseCase
    }
}