package ru.movista

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins
import ru.movista.di.Injector
import ru.movista.di.module.ContextModule
import ru.movista.utils.CrashReportingTree
import ru.movista.utils.FileLogTree
import timber.log.Timber

class App : MultiDexApplication() {

    companion object {
        lateinit var PACKAGE_NAME: String
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        PACKAGE_NAME = this.packageName

        initFabric()
        initLogging()
        initLogExceptionHandler()
        initDagger()
        initThreeTen()
        setRxJavaUndeliverableErrorHandler()

        Timber.i("App start")
    }

    private fun initFabric() {
        val crashlyticsKit = Crashlytics.Builder()
            .core(
                CrashlyticsCore
                    .Builder()
                    .disabled(BuildConfig.DEBUG)
                    .build()
            )
            .build()
        Fabric.with(this, crashlyticsKit)
    }

    private fun initDagger() {
        Injector.initAppComponent(
            ru.movista.di.component.DaggerAppComponent.builder()
                .contextModule(ContextModule(this))
                .build()
        )
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(FileLogTree(this), Timber.DebugTree())
        } else {
            Timber.plant(FileLogTree(this), CrashReportingTree())
        }
    }

    private fun initThreeTen() {
        AndroidThreeTen.init(this)
    }

    private fun setRxJavaUndeliverableErrorHandler() {
        RxJavaPlugins.setErrorHandler {
            Timber.e(it, "Undeliverable exception received, not sure what to do")
        }
    }

    private fun initLogExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(LogExceptionHandler())
    }

    private class LogExceptionHandler : Thread.UncaughtExceptionHandler {

        private val originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        override fun uncaughtException(t: Thread?, e: Throwable?) {
            Timber.i(e) // логируем UncaughtException, чтобы записать в файл. уровень info - чтоб ылог не попал в краш репорты повторно
            originalHandler.uncaughtException(t, e) // затем даем системе обработать исключение (обязательно)
        }
    }
}