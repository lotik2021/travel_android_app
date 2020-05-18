package ru.movista.di.module

import dagger.Module
import dagger.Provides
import ru.movista.di.FlowScope
import ru.movista.presentation.custom.FlowRouter
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Named

@Module
class FlowNavigationModule {

    @Provides
    @FlowScope
    fun provideFlowCicerone(router: Router): Cicerone<FlowRouter> {
        return Cicerone.create(FlowRouter(router))
    }

    @Provides
    @FlowScope
    fun provideRouter(cicerone: Cicerone<FlowRouter>): FlowRouter {
        return cicerone.router
    }

    @Provides
    @Named("FlowNavigatorHolder")
    @FlowScope
    fun provideNavigatorHolder(cicerone: Cicerone<FlowRouter>): NavigatorHolder {
        return cicerone.navigatorHolder
    }
}