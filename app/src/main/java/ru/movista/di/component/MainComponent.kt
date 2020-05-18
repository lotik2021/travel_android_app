package ru.movista.di.component

import android.content.res.AssetManager
import android.content.res.Resources
import dagger.Component
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.framework.LocationManager
import ru.movista.data.framework.OrientationManager
import ru.movista.data.framework.SpeechRecognitionManager
import ru.movista.data.repository.ProfileRepository
import ru.movista.data.repository.SavedInstanceStateRepository
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.ActivityScope
import ru.movista.di.OneSignalQualifier
import ru.movista.di.module.MainModule
import ru.movista.domain.usecase.tickets.TicketsUseCase
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.main.MainActivity
import ru.movista.presentation.main.MainPresenter
import ru.movista.utils.AuthorizationHelper
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Named

@ActivityScope
@Component(
    dependencies = [AppComponent::class],
    modules = [MainModule::class]
)
interface MainComponent {

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
    fun provideProfileRepository(): ProfileRepository
    fun getAuthHelper(): AuthorizationHelper
    fun getTicketsUseCase(): TicketsUseCase

    @OneSignalQualifier
    fun getOneSignalKeyValueStorage(): KeyValueStorage

    fun inject(mainActivity: MainActivity)
    fun inject(mainPresenter: MainPresenter)

    fun inject(baseFragment: BaseFragment)
}