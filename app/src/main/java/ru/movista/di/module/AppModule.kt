package ru.movista.di.module

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import ru.movista.data.framework.LocationManager
import ru.movista.data.framework.OrientationManager
import ru.movista.data.framework.SpeechRecognitionManager
import ru.movista.data.repository.SavedInstanceStateRepository
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.di.OneSignalQualifier
import ru.movista.utils.AuthorizationHelper
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun provideSharedPrefs(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    @OneSignalQualifier
    fun provideOneSignalSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("one_signal_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @OneSignalQualifier
    fun provideOneSignalKeyValueStorage(
        @OneSignalQualifier sharedPreferences: SharedPreferences
    ): KeyValueStorage {
        return KeyValueStorage(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideKeyValueStorage(sharedPreferences: SharedPreferences): KeyValueStorage {
        return KeyValueStorage(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAssets(context: Context): AssetManager {
        return context.assets
    }

    @Provides
    @Singleton
    fun provideSavedInstanceStateRepository(): SavedInstanceStateRepository {
        return SavedInstanceStateRepository()
    }

    @Provides
    @Singleton
    fun provideDeviceInfo(context: Context): DeviceInfo {
        return DeviceInfo(context)
    }

    // разным частям приложения необходимо получать обновление локации с разной частотой -> не Singleton
    @Provides
    fun provideLocationManager(context: Context): LocationManager {
        return LocationManager(context)
    }

    @Provides
    @Singleton
    fun provideOrientationManager(context: Context): OrientationManager {
        return OrientationManager(context)
    }

    @Provides
    @Singleton
    fun provideSpeechRecognitionManager(context: Context): SpeechRecognitionManager {
        return SpeechRecognitionManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthHelper(keyValueStorage: KeyValueStorage, deviceInfo: DeviceInfo): AuthorizationHelper {
        return AuthorizationHelper(keyValueStorage, deviceInfo)
    }
}