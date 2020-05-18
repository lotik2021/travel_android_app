package ru.movista.presentation.main

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.movista.data.entity.LastLocationEventFailure
import ru.movista.data.entity.LastLocationEventSuccess
import ru.movista.data.framework.LocationManager
import ru.movista.data.repository.MainRepository
import ru.movista.data.repository.PingLocationRepository
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.utils.AuthorizationHelper
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    override val screenTag: String
        get() = Screens.Main.TAG

    private val pingLocationDisposable = CompositeDisposable()

    private var canReceiveLocationUpdates = false

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var mainRepository: MainRepository

    @Inject
    lateinit var pingLocationRepository: PingLocationRepository

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var authorizationHelper: AuthorizationHelper

    private var locationDisposable = Disposables.disposed()

    override fun onPresenterInject() {
        Injector.mainComponent?.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        pingLocationDisposable.clear()
        locationDisposable.dispose()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        subscribeToFatalServerError()

        tryToPingLocation()
    }

    fun onAppResume() {
        if (canReceiveLocationUpdates) locationManager.listenToLocationUpdatesIfPossible()
    }

    fun onAppPause() {
        if (canReceiveLocationUpdates) locationManager.stopLocationUpdates()
    }

    fun coldStart() {
        if (mainRepository.isAppFirstRun()) {
            router.newRootScreen(Screens.Intro())
        } else {
            router.newRootScreen(Screens.Chat())
        }
    }

    private fun subscribeToFatalServerError() {
        addDisposable(
            authorizationHelper.getAuthErrorObservable()
                .subscribe {
                    postOnMainThread {
                        // если мы здесь, то значит access_token не обновился, и нет смысла запрашивать pingLocation,
                        // так как нас рекурсивно будет перекидывать на intro
                        locationDisposable.dispose()
                        mainRepository.clearAppData()
                        router.newRootScreen(Screens.Intro())
                    }
                }
        )
    }

    private fun tryToPingLocation() {
        Timber.d("tryToPingLocation")
        if (!locationManager.isLocationPermissionGranted()) return

        // подписываемся на обновления локации
        locationDisposable.dispose()
        locationDisposable =
            locationManager.getLasLocationObservable()
                .observeOn(Schedulers.io())
                .doOnSubscribe { locationManager.listenToLocationUpdatesIfPossible() }
                .subscribe(
                    {
                        when (it) {
                            is LastLocationEventSuccess -> {
                                pingLocation(it)
                            }
                            is LastLocationEventFailure -> {
                                // неозможно получть данные о местоположении
                                Timber.i("locationManager failure: $it")
                            }
                        }
                    },
                    {}
                )
    }

    private fun pingLocation(lastLocationEventSuccess: LastLocationEventSuccess) {
        Timber.i("pingLocation")
        pingLocationDisposable.add(
            pingLocationRepository.pingLocation(lastLocationEventSuccess.lat, lastLocationEventSuccess.lon)
                .subscribe(
                    // нам не важны результаты отправки локации
                    { }, { }
                )
        )
    }
}