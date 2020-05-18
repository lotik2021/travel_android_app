package ru.movista.domain.usecase.tickets

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observables.ConnectableObservable
import java.util.concurrent.TimeUnit

class TicketsUseCase {
    companion object {
        private const val TICKETS_LIFETIME_MINUTES = 30L
    }

    private lateinit var observable: ConnectableObservable<Long>

    fun startTicketsLifetimeTimer(): ConnectableObservable<Long> {
        observable = Observable.timer(TICKETS_LIFETIME_MINUTES, TimeUnit.MINUTES)
            .observeOn(AndroidSchedulers.mainThread())
            .publish()
        observable.connect()
        return observable
    }

    fun getTicketsLifetimeTimer(): ConnectableObservable<Long> {
        return observable
    }
}
