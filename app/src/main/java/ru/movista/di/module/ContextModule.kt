package ru.movista.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.movista.App
import javax.inject.Singleton

@Module
class ContextModule(private val context: App) {

    @Provides
    @Singleton
    fun provideAppContext(): Context = context

    @Provides
    @Singleton
    fun provideApp(): App = context
}