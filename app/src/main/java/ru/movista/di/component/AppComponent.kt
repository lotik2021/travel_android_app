package ru.movista.di.component

import android.content.res.AssetManager
import android.content.res.Resources
import dagger.Component
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.framework.LocationManager
import ru.movista.data.framework.OrientationManager
import ru.movista.data.framework.SpeechRecognitionManager
import ru.movista.data.repository.SavedInstanceStateRepository
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.OneSignalQualifier
import ru.movista.di.module.AppModule
import ru.movista.di.module.NavigationModule
import ru.movista.di.module.NetworkModule
import ru.movista.di.module.tickets.TicketsModule
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.utils.AuthorizationHelper
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        NavigationModule::class,
        TicketsModule::class
    ]
)
interface AppComponent {

    fun getRouter(): Router
    @Named("AppNavigatorHolder")
    fun getNavigationHolder(): NavigatorHolder

    fun getApi(): Api
    fun getResources(): Resources
    fun getKeyValueStorage(): KeyValueStorage
    fun getSavedInstanceStateRepository(): SavedInstanceStateRepository
    fun provideDeviceInfo(): DeviceInfo
    fun getAssets(): AssetManager
    fun getLocationManager(): LocationManager
    fun getOrientationManager(): OrientationManager
    fun getSpeechRecognitionManager(): SpeechRecognitionManager
    fun getAnalyticsManager(): AnalyticsManager
    fun getAuthHelper(): AuthorizationHelper
    fun getTicketsUseCase(): TicketsUseCase

    @OneSignalQualifier
    fun getOneSignalKeyValueStorage(): KeyValueStorage
}