package ru.movista.di.module

import dagger.Module
import dagger.Provides
import ru.movista.di.FragmentScope
import ru.movista.domain.usecase.IntroUseCase
import ru.movista.domain.usecase.IntroUseCaseImpl
import ru.movista.domain.usecase.SlidePageUseCase

@Module
class IntroModule {

    @Provides
    @FragmentScope
    fun provideSlidePageUserCase(introUseCaseImpl: IntroUseCaseImpl): SlidePageUseCase {
        return introUseCaseImpl
    }

    @Provides
    @FragmentScope
    fun provideIntroUseCase(introUseCaseImpl: IntroUseCaseImpl): IntroUseCase {
        return introUseCaseImpl
    }
}
