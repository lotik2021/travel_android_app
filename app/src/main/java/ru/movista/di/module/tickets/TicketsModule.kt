package ru.movista.di.module.tickets

import dagger.Module
import dagger.Provides
import ru.movista.domain.usecase.tickets.TicketsUseCase
import javax.inject.Singleton

@Module
class TicketsModule {
    @Provides
    @Singleton
    fun provideTicketsUseCase(): TicketsUseCase {
        return TicketsUseCase()
    }
}
