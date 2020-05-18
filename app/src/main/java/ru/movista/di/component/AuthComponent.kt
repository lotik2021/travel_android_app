package ru.movista.di.component

import android.content.res.AssetManager
import android.content.res.Resources
import dagger.Component
import ru.movista.analytics.AnalyticsManager
import ru.movista.data.framework.LocationManager
import ru.movista.data.framework.OrientationManager
import ru.movista.data.repository.SavedInstanceStateRepository
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FlowScope
import ru.movista.di.FragmentScope
import ru.movista.di.OneSignalQualifier
import ru.movista.di.module.AuthModule
import ru.movista.di.module.FlowNavigationModule
import ru.movista.domain.usecase.AuthWithPhoneUseCase
import ru.movista.domain.usecase.RegisterUserUseCase
import ru.movista.domain.usecase.VerifyPhoneUseCase
import ru.movista.presentation.auth.AuthFlowFragment
import ru.movista.presentation.auth.AuthFlowPresenter
import ru.movista.presentation.auth.enterphone.EnterPhonePresenter
import ru.movista.presentation.auth.registration.RegistrationPresenter
import ru.movista.presentation.auth.verifyphone.VerifyPhonePresenter
import ru.movista.presentation.custom.FlowRouter
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named

@FlowScope
@Component(
    dependencies = [MainComponent::class],
    modules = [AuthModule::class, FlowNavigationModule::class]
)
interface AuthFlowComponent {

    fun provideAuthWithPhoneUseCase(): AuthWithPhoneUseCase
    fun provideVerifyPhoneUseCase(): VerifyPhoneUseCase
    fun provideRegisterUserUseCase(): RegisterUserUseCase

    fun provideFlowRouter(): FlowRouter

    @Named("FlowNavigatorHolder")
    fun getNavigationHolder(): NavigatorHolder

    fun getApi(): Api
    fun getResources(): Resources
    fun getKeyValueStorage(): KeyValueStorage
    fun getSavedInstanceStateRepository(): SavedInstanceStateRepository
    fun provideDeviceInfo(): DeviceInfo
    fun getAssets(): AssetManager
    fun getLocationManager(): LocationManager
    fun getOrientationManager(): OrientationManager
    fun getAnalyticsManager(): AnalyticsManager

    fun inject(authFlowFragment: AuthFlowFragment)
    fun inject(authFlowPresenter: AuthFlowPresenter)
}

@FragmentScope
@Component(
    dependencies = [AuthFlowComponent::class]
)
interface EnterPhoneComponent {
    fun inject(enterPhonePresenter: EnterPhonePresenter)
}

@FragmentScope
@Component(
    dependencies = [AuthFlowComponent::class]
)
interface VerifyPhoneComponent {
    fun inject(verifyPhonePresenter: VerifyPhonePresenter)
}

@FragmentScope
@Component(
    dependencies = [AuthFlowComponent::class]
)
interface RegistrationComponent {
    fun inject(registrationPresenter: RegistrationPresenter)
}
